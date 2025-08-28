package shympyo.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
public class RentalResponse {

    private Long rentalId;
    private String placeName;
    private LocalDateTime startTime;

}
