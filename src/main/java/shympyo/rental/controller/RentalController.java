package shympyo.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // (선택) JWT 표시
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter; // principal 숨기기
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.CursorPageResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.rental.dto.*;
import shympyo.rental.service.RentalService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rental")
@Tag(name = "Rental", description = "QR 기반 대여(입장/퇴장) API")
@SecurityRequirement(name = "bearerAuth")
public class RentalController {

    private final RentalService rentalService;

    @Operation(
            summary = "입장 처리",
            description = "QR 코드(placeCode)로 장소에 입장한다.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserEnterRequest.class),
                            examples = @ExampleObject(
                                    name = "예시",
                                    value = "{ \"placeCode\": \"PL-A12F9C3D\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "입장 성공",
                            content = @Content(schema = @Schema(implementation = UserEnterResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "장소/대여 정보 없음")
            }
    )
    @PostMapping("/enter")
    public ResponseEntity<?> enter(
            @org.springframework.web.bind.annotation.RequestBody UserEnterRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    ) {
        UserEnterResponse response = rentalService.startRental(user.getId(), request.getPlaceCode());
        return ResponseUtil.success(response);
    }

    @Operation(
            summary = "퇴장 처리",
            description = "현재 대여 중인 장소에서 퇴장한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "퇴장 성공",
                            content = @Content(schema = @Schema(implementation = UserExitResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "404", description = "진행 중 대여 없음")
            }
    )
    @PostMapping("/exit")
    public ResponseEntity<?> exit(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    ) {
        UserExitResponse rental = rentalService.endRental(user.getId());
        return ResponseUtil.success(rental);
    }

    @Operation(
            summary = "현재 이용자 조회",
            description = "내가 소유한 장소에서 현재 이용(대여) 중인 사용자 목록을 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = CurrentRentalResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음")
            }
    )
    @GetMapping
    public ResponseEntity<?> getRental(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    ) {
        List<CurrentRentalResponse> rental = rentalService.getCurrentRental(user.getId());
        return ResponseUtil.success(rental);
    }

    @Operation(
            summary = "이용 내역 전체 조회",
            description = "내가 소유한 장소의 전체 이용(대여) 내역을 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = RentalHistoryResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<?> getTotalRental(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    ) {
        List<RentalHistoryResponse> rental = rentalService.getTotalRental(user.getId());
        return ResponseUtil.success(rental);
    }

    @Operation(
            summary = "사용자 대여 이력(커서 기반 페이지네이션)",
            description = """
            최근 종료된 대여 이력을 커서 기반으로 조회한다.
            다음 페이지를 요청할 때는 이전 응답의 마지막 아이템의 (endTime, rentalId)을 커서로 넘긴다.
            """

    )
    @GetMapping("/user/history")
    public ResponseEntity<CommonResponse<CursorPageResponse<UserRentalHistoryResponse>>> history(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "ended") String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorEndTime,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseUtil.success(
                rentalService.getUserRental(user.getId(), status, cursorEndTime, cursorId, size)
        );
    }

}
