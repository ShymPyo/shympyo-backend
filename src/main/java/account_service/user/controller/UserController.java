package account_service.user.controller;

import account_service.auth.dto.KakaoUserInfo;
import account_service.auth.dto.TokenResponse;
import account_service.auth.user.CustomUserDetails;
import account_service.global.response.CommonResponse;
import account_service.global.response.ResponseUtil;
import account_service.user.dto.SignUpRequest;
import account_service.user.dto.UpdateUserRequest;
import account_service.user.dto.UserInfoResponse;
import account_service.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<TokenResponse>> signup(@RequestBody @Valid SignUpRequest request){
        TokenResponse token = userService.signUp(request);

        return ResponseEntity.ok(ResponseUtil.success("회원가입이 완료 되었습니다.", token));
    }


    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserInfoResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();
        UserInfoResponse userInfo = userService.getMyInfo(userId);

        return ResponseEntity.ok(ResponseUtil.success(userInfo));
    }

    @PatchMapping("/me")
    public ResponseEntity<CommonResponse<UserInfoResponse>> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateUserRequest request){

        if(request.isEmpty()){
            return ResponseEntity.badRequest().body(ResponseUtil.fail("수정할 필드를 하나 이상 입력해야 합니다."));
        }

        UserInfoResponse updated = userService.updateUserInfo(userDetails.getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("회원 정보 수정 완료",updated));
    }

//    @PostMapping("/oauth")
//    public ResponseEntity<Long> findOrCreateUser(@RequestBody KakaoUserInfo userInfo) {
//        Long userId = userService.findOrCreateByEmail(userInfo);
//        return ResponseEntity.ok(userId);
//    }
}
