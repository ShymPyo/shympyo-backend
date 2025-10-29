package shympyo.rental.dto.sse;

import java.time.LocalDateTime;

public record RentalEndedEvent(
        Long rentalId,
        Long placeId,
        LocalDateTime endTime
) {
}