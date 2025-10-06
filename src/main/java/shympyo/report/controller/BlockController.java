package shympyo.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.report.dto.ProviderBlockUserDetailResponse;
import shympyo.report.dto.ProviderBlockUserResponse;
import shympyo.report.dto.ProviderBlockUserRequest;
import shympyo.report.service.BlockService;

import java.util.List;

@Tag(name = "Provider Block", description = "제공자의 사용자 차단/해제/조회 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/blocks/providers/me")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockservice;

    @Operation(
            summary = "특정 사용자 차단",
            description = "제공자가 특정 사용자를 차단한다. 차단은 PLACE_ONLY로 저장되며 scopeRefId에는 제공자의 placeId가 매핑된다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차단 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "대상 사용자를 찾을 수 없음")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<CommonResponse<Long>> blockUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "차단할 사용자 ID", example = "123") @PathVariable Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "차단 사유/메모 등 요청 본문",
                    content = @Content(
                            schema = @Schema(implementation = ProviderBlockUserRequest.class),
                            examples = @ExampleObject(name = "기본 예시", value = """
                            {
                              "reason": "INAPPROPRIATE",
                              "detail": "이용 중 부적절한 언행으로 인해 차단합니다.",
                              "endAt": "2025-10-13T12:02:24.475509"
                            }
                            """)
                    )
            )
            @RequestBody @jakarta.validation.Valid ProviderBlockUserRequest req
    ) {
        Long sanctionId = blockservice.blockUser(user.getId(), userId, req);
        return ResponseUtil.success("해당 사용자를 차단했습니다.", sanctionId);
    }

    @Operation(
            summary = "내가 차단한 사용자 목록 조회",
            description = "제공자가 차단한 사용자들의 요약 목록을 반환한다. 활성 제재만 반환하도록 구현되어 있다면 그에 맞춰 문서화한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<List<ProviderBlockUserResponse>>> getMyBlockedUsers(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me
    ) {
        List<ProviderBlockUserResponse> users = blockservice.getBlock(me.getId());
        return ResponseUtil.success("차단한 사용자 목록을 조회했습니다.", users);
    }

    @Operation(
            summary = "차단 상세 조회",
            description = "특정 사용자를 왜 차단했는지 상세 정보를 반환한다. 활성 제재만 조회한다면 그 기준을 명시한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "차단 상세 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<ProviderBlockUserDetailResponse>> getBlockDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails me,
            @Parameter(description = "상세를 조회할 사용자 ID", example = "123") @PathVariable Long userId
    ) {
        ProviderBlockUserDetailResponse detail = blockservice.getBlockDetail(me.getId(), userId);
        return ResponseUtil.success("차단 상세를 조회했습니다.", detail);
    }

    @Operation(
            summary = "특정 사용자 차단 해제",
            description = "제공자가 특정 사용자의 차단을 해제한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해제 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "차단 내역 없음")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<CommonResponse> unblockUser(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "차단 해제할 사용자 ID", example = "123") @PathVariable Long userId
    ) {
        blockservice.unblockUser(user.getId(), userId);
        return ResponseUtil.success("해당 사용자의 차단을 해제했습니다.");
    }
}
