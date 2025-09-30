package shympyo.rental.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import shympyo.global.response.CursorPageResponse;
import shympyo.rental.domain.Place;
import shympyo.rental.domain.Rental;
import shympyo.rental.dto.*;
import shympyo.rental.repository.PlaceRepository;
import shympyo.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import shympyo.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final PlaceRepository placeRepository;


    @Transactional
    public UserEnterResponse startRental(Long userId, String placeCode) {

        Place place = placeRepository.findByCodeForUpdate(placeCode)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 장소가 없습니다."));

        User user = userRepository.getReferenceById(userId);

        long usingCount = rentalRepository.countByPlaceIdAndStatus(place.getId(), "using");
        if (usingCount >= place.getMaxCapacity()) {
            throw new IllegalStateException("이 장소는 이미 최대 수용 인원("
                    + place.getMaxCapacity() + "명)에 도달했습니다.");
        }

        Rental rental = Rental.start(place, user, LocalDateTime.now());
        rentalRepository.save(rental);

        return UserEnterResponse.from(rental);
    }


    @Transactional
    public UserExitResponse endRental(Long userId) {

        var actives = rentalRepository.findByUserIdAndStatus(userId, "using");

        if (actives.isEmpty()) {
            throw new IllegalStateException("진행 중인 대여가 없습니다.");
        }
        if (actives.size() > 1) {
            throw new IllegalStateException("진행 중인 대여가 여러 건입니다. 어떤 대여를 종료할지 지정해 주세요.");
        }

        Long rentalId = actives.get(0).getId();
        Rental rental = rentalRepository.findByIdForUpdate(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("대여 정보를 찾을 수 없습니다."));

        if (!"using".equals(rental.getStatus())) return UserExitResponse.from(rental);

        rental.end(LocalDateTime.now());

        return UserExitResponse.from(rental);
    }



    @Transactional(readOnly = true)
    public List<CurrentRentalResponse> getCurrentRental(Long userId){

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        return rentalRepository.findCurrentRentalsWithUserByPlace(place.getId());

    }

    @Transactional(readOnly = true)
    public List<RentalHistoryResponse> getTotalRental(Long userId){

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
            Long userId, String status, LocalDateTime cursorEndTime, Long cursorId, int size) {

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


        List<UserRentalHistoryResponse> content = rentals.getContent().stream()
                .map(r -> UserRentalHistoryResponse.builder()
                        .rentalId(r.getId())
                        .placeId(r.getPlace().getId())
                        .placeName(r.getPlace().getName())
                        .startTime(r.getStartTime())
                        .endTime(r.getEndTime())
                        .build())
                .toList();

        return new CursorPageResponse<>(content, rentals.hasNext());
    }

    
    
}
