package shympyo.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import shympyo.auth.dto.SocialLoginResult;
import shympyo.auth.dto.TokenResponse;
import shympyo.auth.service.KakaoOAuthService;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/kakao")
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "소셜 로그인 (카카오)")
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @Operation(
            summary = "카카오 인가 코드 교환",
            description = "프론트가 받은 인가 코드(code)를 백엔드로 전달하면, 백엔드가 카카오 토큰 엔드포인트로 교환한다.",
            security = {} // 전역 JWT 제외
    )
    @GetMapping("/callback")
    public ResponseEntity<CommonResponse<TokenResponse>> callback(@RequestParam("code") String code){

        SocialLoginResult result = kakaoOAuthService.kakaoLogin(code);
        String message = result.isNewUser() ? "회원가입이 완료되었습니다." : "로그인에 성공했습니다.";
        TokenResponse token = new TokenResponse(result.getAccessToken(), result.getRefreshToken());

        return ResponseUtil.success(message, token);
    }
}
