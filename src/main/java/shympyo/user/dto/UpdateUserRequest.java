package shympyo.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Schema(description = "회원 정보 수정 요청 DTO")
public class UpdateUserRequest {

    @Schema(description = "회원 이름 (수정 시 전달)", example = "홍길동")
    private String name;

    @Schema(description = "회원 전화번호 (수정 시 전달)", example = "010-1234-5678")
    private String phone;

    public boolean isEmpty() {
        return name == null && phone == null;
    }

}
