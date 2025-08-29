package shympyo.letter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Value;

@Getter
public class SendLetterRequest {

    @NotNull(message = "장소 ID는 필수입니다.")
    Long placeId;

    @NotBlank(message = "메세지 내용은 필수입니다.")
    String content;

}
