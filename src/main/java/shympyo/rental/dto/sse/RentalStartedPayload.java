package shympyo.rental.dto.sse;

public record RentalStartedPayload(
        Long rentalId, Long placeId, Long userId, String userName, java.time.LocalDateTime startedAt
) {}