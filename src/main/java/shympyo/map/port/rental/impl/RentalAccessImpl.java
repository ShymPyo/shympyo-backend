package shympyo.map.port.rental.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shympyo.map.domain.PlaceType;
import shympyo.map.port.rental.RentalAccess;
import shympyo.rental.domain.Place;
import shympyo.rental.domain.PlaceBusinessHour;
import shympyo.rental.domain.RentalStatus;
import shympyo.rental.repository.PlaceBusinessHourRepository;
import shympyo.rental.repository.PlaceRepository;
import shympyo.rental.repository.RentalRepository;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import shympyo.report.domain.SanctionScope;
import shympyo.report.domain.SanctionType;
import shympyo.report.repository.SanctionRepository;

@Component
@RequiredArgsConstructor
public class RentalAccessImpl implements RentalAccess {

    private final PlaceRepository placeRepository;
    private final RentalRepository rentalRepository;
    private final PlaceBusinessHourRepository placeBusinessHourRepository;
    private final SanctionRepository sanctionRepository;

    @Override
    public RentalPlaceDetailDto getPlaceDetail(Long placeId, LocalDateTime now) {

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 없습니다. id=" + placeId));

        long using = rentalRepository.countByPlaceIdAndStatus(placeId, RentalStatus.USING);

        DayOfWeek todayDow = now.getDayOfWeek();

        PlaceBusinessHour today = placeBusinessHourRepository
                .findByPlaceIdAndDayOfWeek(placeId, todayDow)
                .orElseThrow(() -> new IllegalArgumentException(
                        "영업 시간이 없습니다. placeId=%d, day=%s".formatted(placeId, todayDow)));

        if (!today.isClosed() && (today.getOpenTime() == null || today.getCloseTime() == null)) {
            throw new IllegalStateException(
                    "영업 정보가 불완전합니다. placeId=%d, day=%s".formatted(placeId, todayDow));
        }

        List<DayOfWeek> holidayDays = placeBusinessHourRepository
                .findByPlaceIdAndClosedTrue(placeId).stream()
                .map(PlaceBusinessHour::getDayOfWeek)
                .sorted(Comparator.comparingInt(DayOfWeek::getValue))
                .toList();

        int current = (int) Math.min(
                using,
                place.getMaxCapacity() == null ? using : place.getMaxCapacity()
        );

        return new RentalPlaceDetailDto(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getContent(),
                place.getMaxUsageMinutes(),
                place.getMaxCapacity(),
                current,
                place.getLatitude(),
                place.getLongitude(),
                place.getImageUrl(),
                today.isClosed(),
                today.getDayOfWeek(),
                today.getOpenTime(),
                today.getCloseTime(),
                today.getBreakStart(),
                today.getBreakEnd(),
                holidayDays
        );
    }

    @Override
    public List<RentalPlaceDto> findUserSheltersInBox(
            double minLat, double maxLat,
            double minLon, double maxLon,
            Long userId,
            LocalDateTime now
    ) {
        return placeRepository.findInBoundingBox(minLat, maxLat, minLon, maxLon).stream()
                .filter(p -> !sanctionRepository.existsActivePlaceBlock(
                        userId,
                        SanctionType.BLOCK_CONTENT,
                        SanctionScope.PLACE_ONLY,
                        p.getId(),
                        now
                ))
                .map(p -> new RentalPlaceDto(
                        p.getId(),
                        p.getName(),
                        p.getAddress(),
                        p.getContent(),
                        p.getLatitude(),
                        p.getLongitude(),
                        PlaceType.USER_SHELTER
                ))
                .toList();
    }



}
