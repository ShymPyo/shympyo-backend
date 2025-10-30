package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "사용자 대여 이력 응답 DTO")
public class UserRentalHistoryResponse {

    @Schema(description = "대여 ID", example = "101")
    private Long rentalId;

    @Schema(description = "장소 ID", example = "P-2025-001")
    private Long placeId;

    @Schema(description = "장소 이름", example = "강남역 무인 스터디룸")
    private String placeName;

    @Schema(description = "장소 사진", example = "https://example.com")
    private String imageUrl;

    @Schema(description = "대여 시작 시간", example = "2025-09-01T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "대여 종료 시간", example = "2025-09-01T12:00:00")
    private LocalDateTime endTime;

    @Schema(description = "대여에 대한 편지 작성 여부", example = "true")
    private Boolean isWritten;
}
