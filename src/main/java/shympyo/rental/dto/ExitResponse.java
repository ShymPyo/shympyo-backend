package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "퇴장 응답 DTO")
public class ExitResponse {

    @Schema(description = "대여 ID", example = "123")
    private Long rentalId;

    @Schema(description = "장소 이름", example = "쉼표 스터디카페 2호점")
    private String placeName;

    @Schema(description = "입장 시각", example = "2025-09-20T14:00:00")
    private LocalDateTime startTime;

    @Schema(description = "퇴장 시각", example = "2025-09-20T16:30:00")
    private LocalDateTime endTime;
}
