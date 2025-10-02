package shympyo.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "받은 편지 개수 응답 DTO")
public class LetterCountResponse {

    @Schema(description = "총 받은 편지 개수", example = "42")
    private Long total;

    @Schema(description = "읽지 않은 편지 개수", example = "10")
    private Long unRead;

    @Schema(description = "읽은 편지 개수", example = "32")
    private Long read;
}
