package account_service.user.service;

import account_service.auth.dto.KakaoUserInfo;
import account_service.auth.dto.NaverUserInfo;
import account_service.user.domain.UserRole;
import account_service.user.dto.*;
import account_service.auth.jwt.JwtTokenProvider;
import account_service.auth.domain.RefreshToken;
import account_service.auth.dto.TokenResponse;
import account_service.auth.repository.RefreshTokenRepository;
import account_service.user.domain.User;
import account_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public void signUp(SignUpRequest request){

        // 존재 여부 판단
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .phone(request.getPhone())
                .build();

        userRepository.save(user);

    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(); // 새로 만들 예정

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .build()
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    public void logout(Long userId) {
        if (refreshTokenRepository.existsById(userId)) {
            refreshTokenRepository.deleteById(userId);
        }
    }

    public UserInfoResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return new UserInfoResponse(user);
    }

    public TokenResponse reissue(String oldRefreshToken) {
        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        RefreshToken saved = refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getToken().equals(oldRefreshToken))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("리프레시 토큰이 존재하지 않습니다."));

        User user = userRepository.findById(saved.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새 토큰들 발급
        String newAccessToken = jwtTokenProvider.generateToken(user.getId(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();

        // DB 갱신
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(newRefreshToken)
                        .build()
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public UserInfoResponse updateUserInfo(Long userId, UpdateUserRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if(request.getName() != null){
            user.setName(request.getName());
        }

        if(request.getPhone() != null){
            user.setPhone(request.getPhone());
        }

        return new UserInfoResponse(user);

    }

    public Long findOrCreateByEmail(KakaoUserInfo userInfo) {
        return userRepository.findByEmail(userInfo.email())
                .map(User::getId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(userInfo.email())
                            .name(userInfo.name())
                            .password(null) // 소셜 로그인은 비번 X
                            .role(UserRole.USER) // 기본 역할 부여
                            .phone(userInfo.phone()) // ← 기본 값 넣어주기
                            .build();
                    return userRepository.save(newUser).getId();
                });
    }

    public Long findOrCreateByEmail(NaverUserInfo userInfo) {
        return userRepository.findByEmail(userInfo.email())
                .map(User::getId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(userInfo.email())
                            .name(userInfo.name())
                            .password(null) // 소셜 로그인은 비번 X
                            .role(UserRole.USER) // 기본 역할 부여
                            .phone(userInfo.phone()) // ← 기본 값 넣어주기
                            .build();
                    return userRepository.save(newUser).getId();
                });
    }
}
