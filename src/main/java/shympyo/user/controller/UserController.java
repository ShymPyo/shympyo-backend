package shympyo.user.controller;

import shympyo.auth.dto.TokenResponse;
import shympyo.auth.user.CustomUserDetails;
import shympyo.global.response.CommonResponse;
import shympyo.global.response.ResponseUtil;
import shympyo.user.dto.SignUpRequest;
import shympyo.user.dto.UpdateUserRequest;
import shympyo.user.dto.UserInfoResponse;
import shympyo.user.service.UserService;
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

        return ResponseUtil.success("회원가입이 완료되었습니다.",token);
    }


    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserInfoResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails){

        Long userId = userDetails.getId();
        UserInfoResponse userInfo = userService.getMyInfo(userId);

        return ResponseUtil.success(userInfo);
    }

    @PatchMapping("/me")
    public ResponseEntity<CommonResponse<UserInfoResponse>> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateUserRequest request){

        if(request.isEmpty()){
            return ResponseUtil.fail("수정할 필드를 하나 이상 입력해야 합니다.");
        }

        UserInfoResponse updated = userService.updateUserInfo(userDetails.getId(), request);

        return ResponseUtil.success("회원 정보 수정 완료",updated);
    }

}
