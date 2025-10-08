package shympyo.letter.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.global.response.CursorPageResponse;
import shympyo.letter.domain.Letter;
import shympyo.letter.dto.*;
import shympyo.letter.repository.LetterRepository;
import shympyo.rental.domain.Place;
import shympyo.rental.domain.Rental;
import shympyo.rental.domain.RentalStatus;
import shympyo.rental.repository.RentalRepository;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import shympyo.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public SendLetterResponse send(Long writerId, SendLetterRequest req) {

        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Rental rental = rentalRepository.findByIdAndUserId(req.getRentalId(), writerId)
                    .orElseThrow(() -> new AccessDeniedException("본인의 대여만 편지 작성이 가능합니다."));

        if (!rental.getPlace().getId().equals(req.getPlaceId())) {
            throw new IllegalArgumentException("요청한 placeId와 rental의 place가 일치하지 않습니다.");
        }

        if (!rental.getStatus().equals(RentalStatus.ENDED)) {
            throw new AccessDeniedException("대여 종료 후에만 편지를 보낼 수 있습니다.");
        }

        if (letterRepository.existsByRentalId(rental.getId())) {
            throw new IllegalStateException("이미 해당 대여에 대한 편지가 존재합니다.");
        }

        Letter saved;
        try {
            saved = letterRepository.save(
                    Letter.builder()
                            .writer(writer)
                            .rental(rental)
                            .content(req.getContent())
                            .isRead(false)
                            .build()
            );
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("이미 해당 대여에 대한 편지가 존재합니다.");
        }

        Place p = rental.getPlace();
        return new SendLetterResponse(
                saved.getId(),
                p.getId(),
                p.getName(),
                writer.getId(),
                writer.getName(),
                saved.getContent(),
                saved.isRead(),
                saved.getReadAt(),
                saved.getCreatedAt()
        );

    }

    @Transactional(readOnly = true)
    public CursorPageResponse<LetterHistoryResponse> getReceivedLetters(Long ownerId,
                                                                        @Nullable LocalDateTime cursorCreatedAt,
                                                                        @Nullable Long cursorId,
                                                                        int size) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (owner.getRole() != UserRole.PROVIDER) {
            throw new AccessDeniedException("제공자 권한이 필요합니다.");
        }

        Pageable pageable = PageRequest.of(0,size);

        Slice<Letter> slice = (cursorCreatedAt == null || cursorId == null)
                ? letterRepository.findReceivedByOwner(ownerId, pageable)
                : letterRepository.findReceivedByOwnerWithCursor(ownerId, cursorCreatedAt, cursorId, pageable);

        List<LetterHistoryResponse> history = slice.getContent().stream()
                .map(l -> {
                    var w = l.getWriter();
                    return new LetterHistoryResponse(
                            l.getId(),
                            new WriterInfo(w.getId(), w.getNickname(), w.getBio(), w.getImageUrl()),
                            l.getCreatedAt(),
                            l.isRead()
                    );
                })
                .toList();

        return new CursorPageResponse<>(history, slice.hasNext());

    }



    @Transactional
    public LetterDetailResponse getReceivedLetterDetail(Long ownerId, Long letterId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (owner.getRole() != UserRole.PROVIDER) {
            throw new AccessDeniedException("제공자 권한이 필요합니다.");
        }

        Letter letter = letterRepository.findDetailById(letterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 편지입니다."));

        Long receiverId = letter.getRental().getPlace().getOwner().getId();
        if (!receiverId.equals(ownerId)) {
            throw new AccessDeniedException("수신자만 조회할 수 있습니다.");
        }

        if (!letter.isRead()) {
            letter.markAsRead(LocalDateTime.now());
        }

        var w = letter.getWriter();
        var writerInfo = new WriterInfo(
                w.getId(),
                w.getNickname(),
                w.getBio(),
                w.getImageUrl()
        );

        return new LetterDetailResponse(
                letter.getId(),
                letter.getContent(),
                writerInfo,
                letter.getCreatedAt()
        );
    }



    @Transactional
    public void readLetter(Long ownerId, Long letterId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (owner.getRole() != UserRole.PROVIDER) throw new AccessDeniedException("제공자 권한이 필요합니다.");

        Letter letter = letterRepository.findDetailById(letterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 편지입니다."));

        Long receiverId = letter.getRental().getPlace().getOwner().getId();
        if (!receiverId.equals(ownerId)) {
            throw new AccessDeniedException("수신자만 읽음 처리할 수 있습니다.");
        }

        if (!letter.isRead()) {
            letter.markAsRead(LocalDateTime.now());
        }
        else {
            throw new IllegalArgumentException("이미 읽은 편지입니다.");
        }

    }

    public LetterCountResponse countLetter(Long ownerId){

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (owner.getRole() != UserRole.PROVIDER) {
            throw new AccessDeniedException("제공자 권한이 필요합니다.");
        }

        Long all = letterRepository.countAllByOwner(ownerId);
        Long unread = letterRepository.countUnreadByOwner(ownerId);
        Long read = all - unread;

        LetterCountResponse response = new LetterCountResponse(all, unread, read);

        return response;
    }


    private String maskPhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("(\\d{3})-?(\\d{3,4})-?(\\d{4})", "$1-****-$3");
    }

}
