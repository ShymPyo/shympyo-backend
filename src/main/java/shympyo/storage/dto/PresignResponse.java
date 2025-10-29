package shympyo.storage.dto;

public record PresignResponse(
        String uploadUrl,
        String objectKey,
        String publicUrl
) {}