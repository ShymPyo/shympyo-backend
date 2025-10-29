package shympyo.rental.dto;

public record PlaceImagePresignRequest(
        Long placeId,
        String fileExtension,
        String contentType
) {
}

