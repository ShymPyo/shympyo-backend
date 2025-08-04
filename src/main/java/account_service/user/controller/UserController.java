package account_service.user.controller;

import account_service.auth.user.CustomUserDetails;
import account_service.user.dto.SignUpRequest;
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
    public void signup(@RequestBody @Valid SignUpRequest request){
        userService.signUp(request);
    }


    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();
        return ResponseEntity.ok(userService.getMyInfo(userId));
    }


}
