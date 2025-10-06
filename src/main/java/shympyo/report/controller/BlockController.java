package shympyo.report.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.report.dto.ProviderBlockUserDetailResponse;
import shympyo.report.dto.ProviderBlockUserRequest;
import shympyo.report.dto.ProviderBlockUserResponse;
import shympyo.report.service.BlockService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blocks")
public class BlockController {

    private final BlockService blockservice;


    @PostMapping("/{userId}")
    public ResponseEntity<CommonResponse<Long>> blockUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long userId,
            @RequestBody @Valid ProviderBlockUserRequest req
    ){

        Long sanctionId = blockservice.blockUser(user.getId(), userId, req);
        return ResponseUtil.success("해당 사용자를 차단했습니다.", sanctionId);
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<List<ProviderBlockUserResponse>>> getMyBlockedUsers(
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        List<ProviderBlockUserResponse> users =
                blockservice.getBlock(me.getId());
        return ResponseUtil.success("차단한 사용자 목록을 조회했습니다.", users);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<ProviderBlockUserDetailResponse>> getBlockDetail(
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable Long userId
    ) {
        ProviderBlockUserDetailResponse detail =
                blockservice.getBlockDetail(me.getId(), userId);
        return ResponseUtil.success("차단 상세를 조회했습니다.", detail);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<CommonResponse> unblockUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long userId
    ) {
        blockservice.unblockUser(user.getId(), userId);
        return ResponseUtil.success("해당 사용자의 차단을 해제했습니다.");

    }
}
