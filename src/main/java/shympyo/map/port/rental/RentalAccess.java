package shympyo.map.port.rental;

import java.time.DayOfWeek;
import java.time.LocalTime;
import shympyo.map.domain.PlaceType;

import java.time.LocalDateTime;
import java.util.List;

public interface RentalAccess {

    RentalPlaceDetailDto getPlaceDetail(Long placeId, LocalDateTime now);

    List<RentalPlaceDto> findUserSheltersInBox(
            double minLat, double maxLat,
            double minLon, double maxLon,
            Long userId,
            LocalDateTime now
    );

    record RentalPlaceDto(
            Long id,
            String name,
            String address,
            String content,
            double latitude,
            double longitude,
            PlaceType type
    ) {
    }

    record RentalPlaceDetailDto(
            Long id,
            String name,
            String address,
            String content,
            Integer maxUsageMinutes,
            Integer maxCapacity,
            int currentCapacity,
            double latitude,
            double longitude,
            String imageUrl,
            boolean closedToday,
            DayOfWeek dayOfWeek,
            LocalTime openTime,
            LocalTime closeTime,
            LocalTime breakStart,
            LocalTime breakEnd,
            List<DayOfWeek> holidays
    ) {
    }




}
