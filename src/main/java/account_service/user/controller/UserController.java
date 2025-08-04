package account_service.user.controller;

import account_service.auth.user.CustomUserDetails;
import account_service.user.dto.SignUpRequest;
import account_service.user.dto.UpdateUserRequest;
import account_service.user.dto.UserInfoResponse;
import account_service.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public void signup(@RequestBody @Valid SignUpRequest request){
        userService.signUp(request);
    }


    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();
        return ResponseEntity.ok(userService.getMyInfo(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateUserRequest request){

        if(request.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error","수정할 필드를 하나 이상 입력해야 합니다."));
        }

        userService.updateUserInfo(userDetails.getId(),request);
        return ResponseEntity.ok(Map.of("message","회원 정보 수정 완료"));
    }
}
