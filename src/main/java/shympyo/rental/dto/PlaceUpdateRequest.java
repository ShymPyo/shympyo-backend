package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Schema(description = "장소 수정 요청 DTO (PATCH)")
public class PlaceUpdateRequest {

    @Schema(description = "장소 이름", example = "쉼표 스터디카페 2호점")
    private String name;

    @Schema(description = "장소 설명", example = "조용한 학습 공간입니다.")
    private String content;

    @Schema(description = "최대 수용 인원", example = "12")
    private Integer maxCapacity;

    @Schema(description = "대여 공간 사진(일단 하나만)", example = "s3주소/image/png")
    private String imageUrl;

    @Schema(description = "주소", example = "서울시 강남구 역삼동 123-45")
    private String address;

    @Schema(description = "오픈 시각", example = "09:00:00")
    private LocalTime openTime;

    @Schema(description = "마감 시각", example = "22:00:00")
    private LocalTime closeTime;

    @Schema(description = "주간 휴무일", example = "SUNDAY")
    private DayOfWeek weeklyHoliday;
}
