package shympyo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema
public class LoginRequest {

    @Schema(description = "회원 이메일", example = "user@example.com")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;

    @Schema(description = "회원 비밀번호", example = "P@ssw0rd!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String password;
}
