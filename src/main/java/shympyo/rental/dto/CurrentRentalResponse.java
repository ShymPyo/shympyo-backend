package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class CurrentRentalResponse {

    @Schema(description = "대여 ID", example = "123")
    private Long rentalId;
    private Long userId;
    private String userName;
    private String placeName;
    private LocalDateTime startTime;

}
