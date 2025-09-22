package shympyo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shympyo.user.domain.UserRole;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequest {

    @Schema(description = "회원 이메일", example = "user@example.com")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;

    @Schema(description = "비밀번호 (8~20자)", example = "P@ssw0rd!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String password;

    @Schema(description = "회원 이름", example = "홍길동")
    @NotBlank(message = "이름은 필수 입력입니다.")
    private String name;

    @Schema(description = "전화번호", example = "010-1234-5678")
    @NotBlank(message = "전화번호는 필수 입력입니다.")
    private String phone;

    @Schema(description = "회원 닉네임", example = "길동이")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String imageUrl;

    @Schema(description = "자기소개", example = "안녕하세요! 여행을 좋아하는 홍길동입니다.")
    private String bio;

    @Schema(description = "회원 역할 (기본 CUSTOMER)", example = "USER", defaultValue = "USER")
    private UserRole role = UserRole.USER;
}
