package shympyo.rental.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExitResponse {

    private Long rentalId;
    private String placeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
