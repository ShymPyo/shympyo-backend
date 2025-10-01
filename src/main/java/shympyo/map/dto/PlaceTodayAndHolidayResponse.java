package shympyo.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Builder
@Getter
public class PlaceTodayAndHolidayResponse {

    @Schema(description = "오늘 요일", example = "MONDAY")
    private final DayOfWeek dayOfWeek;

    @Schema(description = "오늘 휴무 여부", example = "false")
    private final boolean closed;

    @Schema(description = "영업 시작 시간(휴무일이면 null)")
    private final LocalTime openTime;

    @Schema(description = "영업 종료 시간(휴무일이면 null)")
    private final LocalTime closeTime;

    @Schema(description = "브레이크 시작 시간(없으면 null)")
    private final LocalTime breakStart;

    @Schema(description = "브레이크 종료 시간(없으면 null)")
    private final LocalTime breakEnd;

    @Schema(description = "휴무 요일 목록")
    private final List<DayOfWeek> holidays;

}
