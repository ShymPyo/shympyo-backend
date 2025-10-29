package shympyo.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserImagePresignRequest(
        @NotBlank String fileExtension,
        @NotBlank String contentType
) {
}