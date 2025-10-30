package shympyo.rental.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.rental.domain.RentalStatus;
import shympyo.rental.dto.external.RentalPlaceDetailDto;
import shympyo.rental.dto.external.RentalPlaceDto;
import shympyo.rental.repository.PlaceBusinessHourRepository;
import shympyo.rental.repository.PlaceRepository;
import shympyo.rental.repository.RentalRepository;
import shympyo.report.repository.SanctionRepository;
import shympyo.report.domain.SanctionType;
import shympyo.report.domain.SanctionScope;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalMapQueryService {

    private final PlaceRepository placeRepository;
    private final RentalRepository rentalRepository;
    private final SanctionRepository sanctionRepository;
    private final PlaceBusinessHourRepository placeBusinessHourRepository;

    public List<RentalPlaceDto> findUserSheltersInBox(
            double minLat, double maxLat,
            double minLon, double maxLon,
            Long userId,
            LocalDateTime at
    ) {
        return placeRepository.findInBoundingBox(minLat, maxLat, minLon, maxLon).stream()
                // 제재된 장소는 뺀다
                .filter(p -> !sanctionRepository.existsActivePlaceBlock(
                        userId,
                        SanctionType.BLOCK_CONTENT,
                        SanctionScope.PLACE_ONLY,
                        p.getId(),
                        at
                ))
                .map(p -> new RentalPlaceDto(
                        p.getId(),
                        p.getName(),
                        p.getAddress(),
                        p.getContent(),
                        p.getLatitude(),
                        p.getLongitude()
                ))
                .sorted(Comparator.comparing(RentalPlaceDto::id))
                .toList();
    }

    public RentalPlaceDetailDto getPlaceDetail(Long placeId, LocalDateTime at) {

        var place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 없습니다. id=" + placeId));

        var using = rentalRepository.countByPlaceIdAndStatus(placeId, RentalStatus.USING);

        var todayDow = at.getDayOfWeek();

        var today = placeBusinessHourRepository
                .findByPlaceIdAndDayOfWeek(placeId, todayDow)
                .orElseThrow(() -> new IllegalArgumentException(
                        "영업 시간이 없습니다. placeId=%d, day=%s".formatted(placeId, todayDow)));

        var holidayDays = placeBusinessHourRepository
                .findByPlaceIdAndClosedTrue(placeId).stream()
                .map(h -> h.getDayOfWeek())
                .sorted()
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

}
