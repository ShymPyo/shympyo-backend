package shympyo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReissueRequest {

    @NotBlank(message = "refresh 토큰은 필수입니다.")
    private String refreshToken;
}
