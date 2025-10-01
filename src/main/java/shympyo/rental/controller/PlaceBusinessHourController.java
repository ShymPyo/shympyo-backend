package shympyo.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.rental.dto.BusinessHoursRequest;
import shympyo.rental.dto.BusinessHoursResponse;
import shympyo.rental.service.PlaceBusinessHourService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
@Tag(name = "Place Business Hours")
public class PlaceBusinessHourController {

    private final PlaceBusinessHourService businessHourService;

    @Operation(summary = "요일별 영업시간 조회")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/{placeId}/business-hours")
    public ResponseEntity<CommonResponse<BusinessHoursResponse>> getBusinessHours(
            @PathVariable Long placeId
    ) {
        var res = businessHourService.getBusinessHours(placeId);
        return ResponseUtil.success("OK", res);
    }

    @Operation(summary = "요일별 영업시간 업서트")
    @ApiResponse(responseCode = "200", description = "저장 완료")
    @PatchMapping("/{placeId}/business-hours")
    public ResponseEntity<CommonResponse<Void>> upsertBusinessHours(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long placeId,
            @RequestBody BusinessHoursRequest body
    ) {
        businessHourService.upsertBusinessHours(placeId, user.getId(), body);
        return ResponseUtil.success("요일별 영업시간이 저장되었습니다.", null);
    }
}
