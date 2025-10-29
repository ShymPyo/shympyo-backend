package shympyo.rental.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import shympyo.rental.domain.Place;
import shympyo.rental.domain.RentalStatus;
import shympyo.rental.repository.PlaceBusinessHourRepository;
import shympyo.rental.repository.PlaceRepository;
import shympyo.rental.repository.RentalQueryRepository;

@Service
@RequiredArgsConstructor
public class RentalEnterValidator {

    private final PlaceRepository placeRepository;
    private final PlaceBusinessHourRepository placeBusinessHourRepository;
    private final RentalQueryRepository rentalQueryRepository;

    @Transactional(readOnly = true)
    public void precheck(Long userId, String placeCode) {

        var now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        Place place = placeRepository.findByCode(placeCode)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 장소가 없습니다."));

        boolean open = placeBusinessHourRepository.existsOpenNow(
                place.getId(),
                now.getDayOfWeek(),
                now.toLocalTime()
        );
        if (!open) {
            throw new IllegalStateException("현재 영업 중이 아닙니다.");
        }

        long usingCount = rentalQueryRepository.countUsingByPlace(
                place.getId(),
                RentalStatus.USING
        );
        if (usingCount >= place.getMaxCapacity()) {
            throw new IllegalStateException("이 장소는 이미 최대 수용 인원("
                    + place.getMaxCapacity() + "명)에 도달했습니다.");
        }

        boolean alreadyUsing = rentalQueryRepository.existsUserRentalWithStatus(
                userId,
                RentalStatus.USING
        );
        if (alreadyUsing) {
            throw new IllegalStateException("이미 진행 중인 대여가 있습니다.");
        }

    }
}
