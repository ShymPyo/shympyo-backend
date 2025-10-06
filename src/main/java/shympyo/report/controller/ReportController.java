package shympyo.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.report.dto.ProviderCreateReportRequest;
import shympyo.report.service.ReportService;


@Tag(name = "Report", description = "신고 관련 API (사용자 신고/조회 등)")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "신고 생성",
            description = """
                    제공자가 사용자를 신고합니다.  
                    요청 본문에는 신고 유형, 사유, 상세 내용을 포함해야 합니다.
                    
                    **ReportReason Enum 값**
                    - `ABUSE` : 욕설, 괴롭힘 등
                    - `INAPPROPRIATE` : 부적절한 언행/콘텐츠
                    - `SCAM` : 사기/거짓 정보
                    - `POLICY_VIOLATION` : 정책 위반
                    - `OTHER` : 기타 사유
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 본문 형식 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<CommonResponse> create(@AuthenticationPrincipal CustomUserDetails user,
                                                 @RequestBody ProviderCreateReportRequest request){

        reportService.report(user.getId(), request);

        return ResponseUtil.success("성공적으로 신고했습니다.");
    }


}
