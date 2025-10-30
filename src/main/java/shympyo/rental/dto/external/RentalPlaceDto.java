package shympyo.rental.dto.external;

public record RentalPlaceDto(
        Long id,
        String name,
        String address,
        String content,
        double latitude,
        double longitude
) {
}
