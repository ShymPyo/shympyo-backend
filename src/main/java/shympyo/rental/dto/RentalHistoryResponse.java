package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "대여 이력 응답 DTO")
public class RentalHistoryResponse {

    @Schema(description = "대여 ID", example = "101")
    private Long rentalId;

    @Schema(description = "사용자 ID", example = "501")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "대여 시작 시각", example = "2025-09-20T14:00:00")
    private LocalDateTime startTime;

    @Schema(description = "대여 종료 시각", example = "2025-09-20T16:30:00")
    private LocalDateTime endTime;

    @Schema(description = "대여 상태", example = "ended")
    private String status;

    @Schema(description = "이용 시간(분)", example = "150")
    public Long getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.MINUTES.between(startTime, endTime);
    }

}
