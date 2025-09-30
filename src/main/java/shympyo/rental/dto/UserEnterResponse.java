package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import shympyo.rental.domain.Rental;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "입장(대여 시작) 응답 DTO")
public class UserEnterResponse {

    @Schema(description = "대여 ID", example = "123")
    private Long rentalId;

    @Schema(description = "장소 이름", example = "쉼표 스터디카페 2호점")
    private String placeName;

    @Schema(description = "입장 시각", example = "2025-09-20T14:00:00")
    private LocalDateTime startTime;

    public static UserEnterResponse from(Rental rental) {
        return UserEnterResponse.builder()
                .rentalId(rental.getId())
                .placeName(rental.getPlace().getName())
                .startTime(rental.getStartTime())
                .build();
    }
}
