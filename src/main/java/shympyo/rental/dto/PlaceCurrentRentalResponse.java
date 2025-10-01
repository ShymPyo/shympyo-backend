package shympyo.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "현재 이용 중 대여 응답 DTO")
public class PlaceCurrentRentalResponse {

    @Schema(description = "대여 ID", example = "123")
    private Long rentalId;

    @Schema(description = "사용자 ID", example = "501")
    private Long userId;

    @Schema(description = "사용자 닉네임", example = "진네커")
    private String nickname;

    @Schema(description = "자기소개", example = "안녕하세요!")
    private String bio;

    @Schema(description = "프로필 이미지", example = "default_image")
    private String imageUrl;

    @Schema(description = "대여 시작 시각", example = "2025-09-20T14:00:00")
    private LocalDateTime startTime;
}