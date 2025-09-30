package shympyo.rental.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Builder
public class BusinessHourItemResponse {

    private DayOfWeek dayOfWeek;

    private LocalTime openTime;

    private LocalTime closeTime;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    private boolean closed;

}
