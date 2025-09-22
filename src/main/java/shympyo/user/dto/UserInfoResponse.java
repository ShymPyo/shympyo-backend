package shympyo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;

@Getter
@Schema(description = "회원 정보 응답 DTO")
public class UserInfoResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "회원 이름", example = "홍길동")
    private String name;

    @Schema(description = "회원 닉네임", example = "길동이")
    private String nickname;

    @Schema(description = "회원 전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String imageUrl;

    @Schema(description = "회원 역할", example = "CUSTOMER")
    private UserRole role;

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
        this.imageUrl = user.getImageUrl();
        this.role = user.getRole();
    }
}
