package shympyo.rental.dto.sse;

public record RentalEndedPayload(
        Long rentalId, Long placeId, java.time.LocalDateTime endedAt
) {}
