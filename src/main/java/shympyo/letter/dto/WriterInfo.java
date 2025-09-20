package shympyo.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "편지 작성자 정보 DTO")
public class WriterInfo {

    @Schema(description = "작성자 ID", example = "77")
    private Long id;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String name;

    @Schema(description = "작성자 이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "작성자 전화번호", example = "010-1234-5678")
    private String phone;

}
