package shympyo.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Value;

@Getter
@Schema(description = "편지 전송 요청 DTO")
public class SendLetterRequest {


    @Schema(description = "편지를 보낼 장소 ID", example = "5")
    @NotNull(message = "장소 ID는 필수입니다.")
    private Long placeId;

    @Schema(description = "메세지 내용", example = "오늘 이용 정말 좋았습니다!")
    @NotBlank(message = "메세지 내용은 필수입니다.")
    private String content;

}
