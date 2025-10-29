package shympyo.rental.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.context.ApplicationEventPublisher;

import shympyo.global.response.CursorPageResponse;
import shympyo.letter.repository.LetterRepository;
import shympyo.rental.domain.Place;
import shympyo.rental.domain.Rental;
import shympyo.rental.domain.RentalStatus;
import shympyo.rental.dto.*;
import shympyo.rental.dto.sse.RentalEndedEvent;
import shympyo.rental.dto.sse.RentalStartedEvent;
import shympyo.rental.repository.PlaceBusinessHourQueryRepository;
import shympyo.rental.repository.PlaceBusinessHourRepository;
import shympyo.rental.repository.PlaceRepository;
import shympyo.rental.repository.RentalQueryRepository;
import shympyo.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import shympyo.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalService {


    private final ApplicationEventPublisher publisher;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final PlaceRepository placeRepository;
    private final LetterRepository letterRepository;
    private final PlaceBusinessHourRepository placeBusinessHourRepository;
    private final RentalQueryRepository rentalQueryRepository;
    private final PlaceBusinessHourQueryRepository placeBusinessHourQueryRepository;

    @Transactional
    public UserEnterResponse startRental(Long userId, String placeCode) {

        var now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        Place place = placeRepository.findByCodeForUpdate(placeCode)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 장소가 없습니다."));

        long usingCountAgain = rentalQueryRepository.countUsingByPlace(
                place.getId(),
                RentalStatus.USING
        );
        if (usingCountAgain >= place.getMaxCapacity()) {
            throw new IllegalStateException("방금 만석이 되었습니다.");
        }

        User userRef = userRepository.getReferenceById(userId);

        Rental rental = Rental.start(place, userRef, LocalDateTime.now());
        rentalRepository.save(rental);

        publisher.publishEvent(new RentalStartedEvent(
                rental.getId(),
                place.getId(),
                userRef.getId(),
                userRef.getName(),
                rental.getStartTime()
        ));

        return UserEnterResponse.from(rental);
    }


    @Transactional
    public UserExitResponse endRentalByUserId(Long userId) {

        var actives = rentalRepository.findByUserIdAndStatus(userId, RentalStatus.USING);

        if (actives.isEmpty()) {
            throw new IllegalStateException("진행 중인 대여가 없습니다.");
        }
        if (actives.size() > 1) {
            throw new IllegalStateException("진행 중인 대여가 여러 건입니다. 어떤 대여를 종료할지 지정해 주세요.");
        }

        Long rentalId = actives.get(0).getId();
        Rental rental = rentalRepository.findByIdForUpdate(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("입장 정보를 찾을 수 없습니다."));

        if (!RentalStatus.USING.equals(rental.getStatus())) {
            return UserExitResponse.from(rental);
        }

        rental.end(LocalDateTime.now());

        publisher.publishEvent(new RentalEndedEvent(
                rental.getId(),
                rental.getPlace().getId(),
                rental.getEndTime()
        ));

        return UserExitResponse.from(rental);
    }

    @Transactional
    public UserExitResponse kickRental(Long providerId, Long rentalId) {

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Rental rental = rentalRepository.findByIdForUpdate(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("입장 정보를 찾을 수 없습니다."));

        if (!providerId.equals(rental.getPlace().getOwner().getId())) {
            throw new IllegalStateException("해당 장소 제공자만 퇴장시킬 수 있습니다.");
        }

        if (rental.isCanceled()) {
            return UserExitResponse.from(rental);
        }

        if (rental.isEnded()) {
            throw new IllegalStateException("이미 종료된 대여는 퇴장시킬 수 없습니다.");
        }

        rental.kick(LocalDateTime.now());

        return UserExitResponse.from(rental);
    }


    @Transactional(readOnly = true)
    public List<PlaceCurrentRentalResponse> getCurrentRental(Long userId) {

        var ongoing = java.util.List.of(RentalStatus.USING, RentalStatus.TIME_EXCEEDED);

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        return rentalRepository.findCurrentRentalsWithUserByPlace(place.getId(), ongoing);

    }

    @Transactional(readOnly = true)
    public List<PlaceRentalHistoryResponse> getTotalRental(Long userId, UserRole userRole) {

        if (userRole != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        return rentalQueryRepository.findAllHistoryByPlace(place.getId());

    }

    @Transactional(readOnly = true)
    public CursorPageResponse<UserRentalHistoryResponse> getUserRental(
            Long userId, RentalStatus status, LocalDateTime cursorEndTime, Long cursorId, int size) {

        int pageSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(0, pageSize);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() != UserRole.USER) {
            throw new IllegalArgumentException("사용자가 아닙니다.");
        }

        Slice<Rental> rentals = (cursorEndTime == null)
                ? rentalRepository.findEndedByUser(userId, status, pageable)
                : rentalRepository.findEndedByUserWithCursor(userId, status, cursorEndTime,
                        cursorId == null ? Long.MAX_VALUE : cursorId,
                        pageable);

        List<Rental> rentalList = rentals.getContent();
        List<Long> rentalIds = rentalList.stream().map(Rental::getId).toList();

        Set<Long> rentalIdsWithLetter = rentalIds.isEmpty()
                ? Set.of()
                : letterRepository.findRentalIdsWithLetter(rentalIds);

        List<UserRentalHistoryResponse> content = rentals.getContent().stream()
                .map(r -> UserRentalHistoryResponse.builder()
                        .rentalId(r.getId())
                        .placeId(r.getPlace().getId())
                        .placeName(r.getPlace().getName())
                        .startTime(r.getStartTime())
                        .endTime(r.getEndTime())
                        .isWritten(rentalIdsWithLetter.contains(r.getId()))
                        .build())
                .toList();

        return new CursorPageResponse<>(content, rentals.hasNext());
    }


}
