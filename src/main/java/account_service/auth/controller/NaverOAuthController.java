package account_service.auth.controller;


import account_service.auth.dto.SocialLoginResult;
import account_service.auth.dto.TokenResponse;
import account_service.auth.service.NaverOAuthService;
import account_service.global.response.CommonResponse;
import account_service.global.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/naver")
public class NaverOAuthController {

    private final NaverOAuthService naverOAuthService;

    @GetMapping("/callback")
    public ResponseEntity<CommonResponse<TokenResponse>> callback(@RequestParam("code") String code){

        SocialLoginResult result = naverOAuthService.naverLogin(code);
        String message = result.isNewUser() ? "회원가입이 완료되었습니다." : "로그인에 성공했습니다.";
        TokenResponse token = new TokenResponse(result.getAccessToken(),result.getRefreshToken());

        return ResponseUtil.success(message, token);
    }

}
