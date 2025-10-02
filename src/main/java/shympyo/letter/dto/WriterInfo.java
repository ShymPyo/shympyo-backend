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

    @Schema(description = "작성자 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "자기 소개", example = "안녕하세요 사용자입니다! ")
    private String bio;

    @Schema(description = "작성자 프로필 이미지 주소", example = "https~")
    private String imageUrl;

}
