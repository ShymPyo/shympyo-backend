package shympyo.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter @AllArgsConstructor
public class LetterHistoryResponse {

    @Schema(description = "편지 ID", example = "101")
    private Long letterId;

    @Schema(description = "작성자 정보")
    private WriterInfo writerInfo;

    @Schema(description = "작성 시간", example = "2025-09-19T20:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "읽음 여부", example = "false")
    private boolean isRead;

}
