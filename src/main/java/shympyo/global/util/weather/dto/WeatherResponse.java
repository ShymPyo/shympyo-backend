package shympyo.global.util.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "날씨 정보 응답 DTO")
public class WeatherResponse {

    @Schema(description = "날씨 상태", example = "맑음")
    private String weather;

    @Schema(description = "기온 (섭씨)", example = "23.5")
    private double temperature;

    @Schema(description = "위치 (한글 주소)", example = "서울특별시 종로구 청운효자동")
    private String location;
}