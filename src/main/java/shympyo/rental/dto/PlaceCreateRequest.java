package shympyo.rental.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import shympyo.rental.domain.PlaceStatus;



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
    @Schema(description = "최대 이용 시간(분)", example = "240", defaultValue = "30",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer maxUsageMinutes;

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

    @Schema(description = "상태", example = "INACTIVE")
    private PlaceStatus status;


}
