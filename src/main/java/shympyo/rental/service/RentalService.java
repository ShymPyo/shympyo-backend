package shympyo.rental.service;

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
import shympyo.rental.repository.PlaceBusinessHourRepository;
import shympyo.rental.repository.PlaceRepository;
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

@Service
@RequiredArgsConstructor
public class RentalService {


    private final ApplicationEventPublisher publisher;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final PlaceRepository placeRepository;
    private final LetterRepository letterRepository;
    private final PlaceBusinessHourRepository placeBusinessHourRepository;


    @Transactional
    public UserEnterResponse startRental(Long userId, String placeCode) {

        var now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        Place place = placeRepository.findByCodeForUpdate(placeCode)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 장소가 없습니다."));

        User user = userRepository.getReferenceById(userId);

        boolean open = placeBusinessHourRepository.existsOpenNow(
                place.getId(), now.getDayOfWeek(), now.toLocalTime()
        );

        if(!open) throw new IllegalStateException("현재 영업 중이 아닙니다.");

        long usingCount = rentalRepository.countByPlaceIdAndStatus(place.getId(), RentalStatus.USING);

        if (rentalRepository.existsByUserIdAndStatus(userId, RentalStatus.USING)) {
            throw new IllegalStateException("이미 진행 중인 대여가 있습니다.");
        }

        if (usingCount >= place.getMaxCapacity()) {
            throw new IllegalStateException("이 장소는 이미 최대 수용 인원("
                    + place.getMaxCapacity() + "명)에 도달했습니다.");
        }

        Rental rental = Rental.start(place, user, LocalDateTime.now());
        rentalRepository.save(rental);

        publisher.publishEvent(new RentalStartedEvent(rental.getId()));

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

        if (!RentalStatus.USING.equals(rental.getStatus())) return UserExitResponse.from(rental);

        rental.end(LocalDateTime.now());

        publisher.publishEvent(new RentalEndedEvent(rental.getId()));

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
                .orElseThrow(()-> new IllegalArgumentException("입장 정보를 찾을 수 없습니다."));

        if (!providerId.equals(rental.getPlace().getOwner().getId())){
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
    public List<PlaceCurrentRentalResponse> getCurrentRental(Long userId){

        var ongoing = java.util.List.of(RentalStatus.USING, RentalStatus.TIME_EXCEEDED);

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        return rentalRepository.findCurrentRentalsWithUserByPlace(place.getId(),ongoing);

    }

    @Transactional(readOnly = true)
    public List<PlaceRentalHistoryResponse> getTotalRental(Long userId){

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        return rentalRepository.findAllHistoryByPlace(place.getId());

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
