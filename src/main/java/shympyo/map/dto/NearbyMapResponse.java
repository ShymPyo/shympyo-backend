package shympyo.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import shympyo.map.domain.PlaceType;

@Getter
@AllArgsConstructor
@Schema(description = "주변 장소 지도 응답 DTO (좌표 기반)")
public class NearbyMapResponse {

    @Schema(description = "장소 ID", example = "7")
    private Long id;

    @Schema(description = "위도", example = "37.5665")
    private double latitude;

    @Schema(description = "경도", example = "126.9780")
    private double longitude;

    @Schema(description = "장소 유형", example = "SHELTER")
    private PlaceType type;
}
