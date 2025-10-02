package shympyo.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import shympyo.weather.dto.WeatherResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private static final String OPENWEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json";
    private static final String WEATHER_UNITS = "metric";
    private static final String WEATHER_LANG = "kr";

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherResponse getCurrentWeather(double latitude, double longitude) {
        log.info("날씨 정보 조회 요청 - 위도: {}, 경도: {}", latitude, longitude);

        try {
            String location = getKoreanAddress(latitude, longitude);
            log.debug("주소 변환 완료: {}", location);

            String url = UriComponentsBuilder
                    .fromHttpUrl(OPENWEATHER_API_URL)
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .queryParam("appid", apiKey)
                    .queryParam("units", WEATHER_UNITS)
                    .queryParam("lang", WEATHER_LANG)
                    .build()
                    .toUriString();

            log.debug("OpenWeather API 요청 URL: {}", url.replaceAll("appid=[^&]*", "appid=***"));

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            WeatherResponse weatherResponse = WeatherResponse.builder()
                    .temperature(root.path("main").path("temp").asDouble())
                    .weather(root.path("weather").get(0).path("description").asText())
                    .location(location)
                    .build();

            log.info("날씨 정보 조회 성공: {}", weatherResponse);
            return weatherResponse;

        } catch (RestClientException e) {
            log.error("API 호출 실패 - 위도: {}, 경도: {}, 오류: {}", latitude, longitude, e.getMessage());
            return createErrorResponse(latitude, longitude);
        } catch (Exception e) {
            log.error("날씨 정보 조회 중 예상치 못한 오류 발생", e);
            return createErrorResponse(latitude, longitude);
        }
    }

    private String getKoreanAddress(double latitude, double longitude) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(KAKAO_API_URL)
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .build()
                    .toUriString();

            log.debug("Kakao API 요청 URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            if (documents.isArray() && documents.size() > 0) {
                JsonNode address = documents.get(0);
                JsonNode roadAddress = address.path("road_address");

                if (!roadAddress.isMissingNode()) {
                    String addr = buildAddress(
                            roadAddress.path("region_1depth_name").asText(),
                            roadAddress.path("region_2depth_name").asText(),
                            roadAddress.path("region_3depth_name").asText()
                    );
                    log.debug("도로명 주소 사용: {}", addr);
                    return addr;
                } else {
                    JsonNode jibunAddress = address.path("address");
                    String addr = buildAddress(
                            jibunAddress.path("region_1depth_name").asText(),
                            jibunAddress.path("region_2depth_name").asText(),
                            jibunAddress.path("region_3depth_name").asText()
                    );
                    log.debug("지번 주소 사용: {}", addr);
                    return addr;
                }
            }

            log.warn("Kakao API 응답에 주소 정보 없음");

        } catch (RestClientException e) {
            log.error("Kakao API 호출 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("주소 변환 중 오류 발생", e);
        }

        return "위치 정보 없음";
    }

    private String buildAddress(String region1, String region2, String region3) {
        return String.format("%s %s %s", region1, region2, region3).trim();
    }

    private WeatherResponse createErrorResponse(double latitude, double longitude) {
        return WeatherResponse.builder()
                .weather("정보를 가져올 수 없습니다")
                .temperature(0.0)
                .location(String.format("위도: %.4f, 경도: %.4f", latitude, longitude))
                .build();
    }
}