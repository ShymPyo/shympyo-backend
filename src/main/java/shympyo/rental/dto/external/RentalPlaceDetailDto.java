package shympyo.rental.dto.external;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record RentalPlaceDetailDto(
        Long id,
        String name,
        String address,
        String content,
        Integer maxUsageMinutes,
        Integer maxCapacity,
        Integer currentCapacity,
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
) {}
