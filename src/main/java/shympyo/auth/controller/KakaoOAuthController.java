package shympyo.auth.controller;

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
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/callback")
    public ResponseEntity<CommonResponse<TokenResponse>> callback(@RequestParam("code") String code){

        SocialLoginResult result = kakaoOAuthService.kakaoLogin(code);
        String message = result.isNewUser() ? "회원가입이 완료되었습니다." : "로그인에 성공했습니다.";
        TokenResponse token = new TokenResponse(result.getAccessToken(), result.getRefreshToken());

        return ResponseUtil.success(message, token);
    }
}
