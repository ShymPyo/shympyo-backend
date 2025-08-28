package account_service.auth.controller;

import account_service.auth.user.CustomUserDetails;
import account_service.auth.dto.ReissueRequest;
import account_service.auth.dto.TokenResponse;
import account_service.global.response.CommonResponse;
import account_service.global.response.ResponseUtil;
import account_service.user.dto.LoginRequest;
import account_service.user.service.UserService;
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
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {

        TokenResponse tokenResponse = userService.login(request);

        return ResponseUtil.success("로그인에 성공했습니다.", tokenResponse);

    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.logout(userDetails.getId());

        return ResponseUtil.success("성공적으로 로그아웃했습니다.");

    }

    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<TokenResponse>> reissue(@RequestBody @Valid ReissueRequest request) {

        TokenResponse tokenResponse = userService.reissue(request.getRefreshToken());

        return ResponseUtil.success(tokenResponse);
    }

}
