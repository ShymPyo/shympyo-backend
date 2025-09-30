package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "입장 요청 DTO (QR 코드 기반)")
public class UserEnterRequest {

    @Schema(description = "장소 코드", example = "PLACE-A-001")
    private String placeCode;

}
