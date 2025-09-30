package shympyo.rental.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class BusinessHourUpsertRequest {

    private DayOfWeek dayOfWeek;

    private LocalTime openTime;

    private LocalTime closeTime;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    private boolean closed;

    @AssertTrue(message = "영업시간이 유효하지 않습니다.")
    private boolean isValidOpenClose() {
        if (closed) return true; // 휴무면 무시
        return openTime != null && closeTime != null && openTime.isBefore(closeTime);
    }

    @AssertTrue(message = "휴식 시간이 유효하지 않습니다.")
    private boolean isValidBreak() {
        if (closed) return true; // 휴무면 무시
        if (breakStart == null && breakEnd == null) return true; // 휴식 없음
        if (breakStart == null || breakEnd == null) return false;
        if (!breakStart.isBefore(breakEnd)) return false;
        return !breakStart.isBefore(openTime) && !breakEnd.isAfter(closeTime);
    }

}