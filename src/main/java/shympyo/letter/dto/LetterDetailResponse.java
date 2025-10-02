package shympyo.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
@Schema(description = "받은 편지 응답 DTO")
public class LetterDetailResponse {

    @Schema(description = "편지 ID", example = "101")
    private Long letterId;

    @Schema(description = "편지 내용", example = "오늘 카페 잘 이용했습니다!")
    private String content;

    @Schema(description = "작성자 정보")
    private WriterInfo writerInfo;

    @Schema(description = "작성 시간", example = "2025-09-19T20:30:00")
    private LocalDateTime createdAt;
}
