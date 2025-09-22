package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장소 생성 요청 DTO")
public class PlaceCreateRequest {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "장소 이름", example = "스터디룸 A", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "장소 설명", example = "화이트보드/빔프로젝터 구비")
    private String content;

    @Size(max = 500)
    @Schema(description = "대표 이미지 URL", example = "https://cdn.example.com/places/a.jpg")
    private String imageUrl;

    @NotNull
    @Positive
    @Schema(description = "최대 수용 인원", example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer maxCapacity;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Schema(description = "위도", example = "37.5665", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Schema(description = "경도", example = "126.9780", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;

    @Size(max = 200)
    @Schema(description = "주소", example = "서울특별시 중구 세종대로 110")
    private String address;

    @NotNull
    @Schema(description = "오픈 시간", example = "09:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime openTime;

    @NotNull
    @Schema(description = "마감 시간", example = "22:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime closeTime;

    @Schema(description = "주간 휴무일", example = "SUNDAY")
    private DayOfWeek weeklyHoliday;


}
