package shympyo.user.dto;

public record PresignResponse(
        String uploadUrl,
        String objectKey,
        String publicUrl
) {}