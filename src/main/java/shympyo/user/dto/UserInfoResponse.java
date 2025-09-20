package shympyo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import lombok.Getter;

@Getter
@Schema
public class UserInfoResponse {

    @Schema(description = "회원 ID", example = "1")
    private final Long id;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "회원 이름", example = "홍길동")
    private final String name;

    @Schema(description = "회원 전화번호", example = "010-1234-5678")
    private final String phone;

    @Schema(description = "회원 역할", example = "USER")
    private final UserRole role;

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.role = user.getRole();
    }
}