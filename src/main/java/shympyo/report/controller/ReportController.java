package shympyo.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.report.dto.ProviderCreateReportRequest;
import shympyo.report.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<CommonResponse> create(@AuthenticationPrincipal CustomUserDetails user,
                                                 @RequestBody ProviderCreateReportRequest request){

        reportService.report(user.getId(), request);

        return ResponseUtil.success("성공적으로 신고했습니다.");
    }


}
