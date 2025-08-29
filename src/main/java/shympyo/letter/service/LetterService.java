package shympyo.letter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.letter.domain.Letter;
import shympyo.letter.dto.CountLetterResponse;
import shympyo.letter.dto.LetterResponse;
import shympyo.letter.dto.SendLetterRequest;
import shympyo.letter.repository.LetterRepository;
import shympyo.rental.domain.Place;
import shympyo.rental.repository.PlaceRepository;
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
    private final PlaceRepository placeRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public LetterResponse send(Long writerId, SendLetterRequest request) {

        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));

        boolean visited = rentalRepository.hasEndedRental(writerId, place.getId());
        if (!visited) throw new AccessDeniedException("대여 이력이 없는 장소에는 편지를 보낼 수 없습니다.");

        Letter saved = letterRepository.save(
                Letter.builder()
                        .writer(writer)
                        .place(place)
                        .content(request.getContent())
                        .isRead(false)
                        .build()
        );

        // 여기서 바로 DTO로 변환하면 LAZY 프록시 직렬화 문제 없음
        return new LetterResponse(
                saved.getId(),
                saved.getPlace().getId(),
                saved.getPlace().getName(),
                saved.getWriter().getId(),
                saved.getWriter().getName(),
                saved.getContent(),
                saved.isRead(),
                saved.getReadAt(),
                saved.getCreatedAt()
        );
    }


    public List<LetterResponse> getReceivedLetters(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (owner.getRole() != UserRole.PROVIDER) {
            throw new AccessDeniedException("제공자 권한이 필요합니다.");
        }

        return letterRepository.findAllByOwner(ownerId).stream()
                .map(l -> new LetterResponse(
                        l.getId(),
                        l.getPlace().getId(),
                        l.getPlace().getName(),
                        l.getWriter().getId(),
                        l.getWriter().getName(),
                        l.getContent(),
                        l.isRead(),
                        l.getReadAt(),
                        l.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void readLetter(Long ownerId, Long letterId) {
        if (ownerId == null || letterId == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }

        // 1) 사용자 확인 (선택: 제공자 롤 검증)
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (owner.getRole() != UserRole.PROVIDER) throw new AccessDeniedException("제공자 권한이 필요합니다.");

        // 2) 편지 로드
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 편지입니다."));

        // 3) 권한 확인: 이 편지의 수신자가 맞는지
        Long receiverId = letter.getPlace().getOwner().getId();
        if (!receiverId.equals(ownerId)) {
            throw new AccessDeniedException("수신자만 읽음 처리할 수 있습니다.");
        }


        // 5) 읽음 처리 (idempotent)
        if (!letter.isRead()) {
            letter.markAsRead(LocalDateTime.now());
        }
        else {
            throw new IllegalArgumentException("이미 읽은 편지입니다.");
        }

    }

    // 읽은 편지 개수 확인하기
    public CountLetterResponse countLetter(Long ownerId){

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (owner.getRole() != UserRole.PROVIDER) {
            throw new AccessDeniedException("제공자 권한이 필요합니다.");
        }

        Long all = letterRepository.countAllByOwner(ownerId);
        Long unread = letterRepository.countUnreadByOwner(ownerId);
        Long read = all - unread;

        CountLetterResponse response = new CountLetterResponse(all, unread, read);

        return response;
    }


}
