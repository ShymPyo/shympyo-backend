package shympyo.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import shympyo.map.domain.PlaceType;
import shympyo.rental.domain.PlaceBusinessHour;

import java.util.List;

@Getter
@Builder
@Schema(description = "장소 상세 정보 응답 DTO")
public class PlaceDetailResponse {

    @Schema(description = "장소 ID", example = "7")
    private Long id;

    @Schema(description = "장소 이름", example = "스마트쉘터")
    private String name;

    @Schema(description = "주소", example = "서울특별시 종로구 종로 1")
    private String address;

    @Schema(description = "설명/소개", example = "16-023 강서세무서(중) 버스정류소")
    private String content;

    @Schema(description = "최대 사용 시간", example = "5")
    private Integer maxUsageMinutes;

    @Schema(description = "최대 수용 인원", example = "5")
    private Integer maxCapacity;

    @Schema(description = "현재 이용 인원", example = "3")
    private Integer currentCapacity;

    @Schema(description = "영업 시간")
    private PlaceTodayAndHolidayResponse todayAndHoliday;

    @Schema(description = "이미지 주소")
    private String imageUrl;

    @Schema(description = "위도", example = "37.5665")
    private double latitude;

    @Schema(description = "경도", example = "126.9780")
    private double longitude;

    @Schema(description = "장소 유형", example = "CAFE")
    private PlaceType type;
}
