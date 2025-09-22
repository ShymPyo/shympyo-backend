package shympyo.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import shympyo.user.domain.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Schema(description = "회원 정보 수정 요청 DTO")
public class UpdateUserRequest {

    @Schema(description = "회원 이름 (수정 시 전달)", example = "홍길동")
    private String name;

    @Schema(description = "회원 전화번호 (수정 시 전달)", example = "010-1234-5678")
    private String phone;

    @Schema(description = "회원 닉네임 (수정 시 전달)", example = "길동이")
    private String nickname;

    @Schema(description = "프로필 이미지 URL (수정 시 전달)", example = "https://example.com/profile.jpg")
    private String imageUrl;

    public boolean isEmpty() {
        return name == null && phone == null;
    }

    public void applyTo(User user) {
        if (this.name != null) {
            user.setName(this.name);
        }
        if (this.phone != null) {
            user.setPhone(this.phone);
        }
        if (this.nickname != null) {
            user.setNickname(this.nickname);
        }
        if (this.imageUrl != null) {
            user.setImageUrl(this.imageUrl);
        }
    }
}
