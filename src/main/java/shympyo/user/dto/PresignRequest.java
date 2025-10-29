package shympyo.user.dto;

import jakarta.validation.constraints.NotBlank;

public record PresignRequest(
        @NotBlank String fileExtension,
        @NotBlank String contentType
) {
}