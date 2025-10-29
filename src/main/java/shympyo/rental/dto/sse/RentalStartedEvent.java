package shympyo.rental.dto.sse;

import java.time.LocalDateTime;
import lombok.Getter;

public record RentalStartedEvent(
        Long rentalId,
        Long placeId,
        Long userId,
        String userName,
        LocalDateTime startTime
) {
}