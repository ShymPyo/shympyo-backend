package shympyo.global.util.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.global.util.weather.dto.WeatherResponse;
import shympyo.global.util.weather.service.WeatherService;

@Tag(name = "Weather", description = "날씨 정보 API")
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(
            summary = "현재 날씨 조회",
            description = """
            위도(lat), 경도(lon) 좌표를 기준으로 현재 날씨 정보를 조회한다.
            
            - OpenWeather API를 통해 날씨 정보 조회
            - Kakao API를 통해 한글 주소 변환
            """
    )
    @GetMapping
    public ResponseEntity<CommonResponse<WeatherResponse>> getWeather(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam double lat,

            @Parameter(description = "경도", example = "126.9780")
            @RequestParam double lon) {
        return ResponseUtil.success(weatherService.getCurrentWeather(lat, lon));
    }

    @Operation(
            summary = "날씨 API 상태 확인",
            description = "날씨 API 서버가 정상 작동하는지 확인한다."
    )
    @GetMapping("/test")
    public ResponseEntity<CommonResponse> test() {  // <String> 제거
        return ResponseUtil.success("날씨 API 서버가 정상 작동 중입니다!");
    }
}