package shympyo.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import shympyo.map.domain.PlaceType;

@Getter
@AllArgsConstructor
@Schema(description = "주변 장소 목록 응답 DTO")
public class NearbyListResponse {

    @Schema(description = "장소 ID", example = "7")
    private Long id;

    @Schema(description = "장소 이름", example = "스마트쉘터")
    private String name;

    @Schema(description = "주소", example = "서울 강서구 마곡동 730-314")
    private String address;

    @Schema(description = "설명/소개", example = "16-023 강서세무서(중) 버스정류소")
    private String content;

    @Schema(description = "장소 유형", example = "SHELTER")
    private PlaceType type;

    @Schema(description = "현재 위치로부터 거리 (미터)", example = "85.3")
    private double distanceM;
}
