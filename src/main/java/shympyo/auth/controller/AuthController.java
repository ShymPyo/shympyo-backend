package shympyo.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import shympyo.auth.user.CustomUserDetails;
import shympyo.auth.dto.ReissueRequest;
import shympyo.auth.dto.TokenResponse;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.user.dto.LoginRequest;
import shympyo.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증/인가 관련 API")
public class AuthController {

    private final UserService userService;

    @Operation(
            summary = "일반 로그인",
            description = "이메일과 비밀번호로 로그인하여 AccessToken/RefreshToken을 발급받는다.",
            security = {} // 전역 JWT 보안 제외
    )
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {

        TokenResponse tokenResponse = userService.login(request);

        return ResponseUtil.success("로그인에 성공했습니다.", tokenResponse);

    }

    @Operation(
            summary = "로그아웃",
            description = "현재 로그인한 사용자의 RefreshToken을 무효화한다."
    )
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.logout(userDetails.getId());

        return ResponseUtil.success("성공적으로 로그아웃했습니다.");

    }

    @Operation(
            summary = "토큰 재발급",
            description = "RefreshToken을 이용해 새로운 AccessToken/RefreshToken을 발급받는다.",
            security = {} // 보통 재발급은 AccessToken 만료 상태에서 요청하므로 JWT 보안 제외
    )
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<TokenResponse>> reissue(@RequestBody @Valid ReissueRequest request) {

        TokenResponse tokenResponse = userService.reissue(request.getRefreshToken());

        return ResponseUtil.success(tokenResponse);
    }

}
