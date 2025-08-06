package account_service.auth.service;

import account_service.auth.domain.RefreshToken;
import account_service.auth.dto.SocialLoginResult;
import account_service.auth.dto.TokenResponse;
import account_service.auth.jwt.JwtTokenProvider;
import account_service.auth.repository.RefreshTokenRepository;
import account_service.user.domain.UserRole;
import account_service.auth.dto.KakaoUserInfo;
import account_service.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret; // 추가

    public SocialLoginResult kakaoLogin(String code) {
        String kakaoAccessToken = getAccessToken(code);
        KakaoUserInfo userInfo = getUserInfo(kakaoAccessToken);

        if (userInfo.email() == null || userInfo.email().isBlank()) {
            throw new IllegalArgumentException("이메일 동의가 필요합니다.");
        }

        SocialLoginResult result = userService.findOrCreateByEmail(userInfo);

        String accessToken = jwtTokenProvider.generateToken(result.getUserId(), UserRole.USER);
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        result.setAccessToken(accessToken);
        result.setRefreshToken(refreshToken);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(result.getUserId())
                        .token(refreshToken)
                        .build()
        );

        return result;
    }


    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);


        HttpEntity<?> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token", request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 파싱 실패");
        }
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                String.class
        );

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            String kakaoId = json.get("id").asText();

            JsonNode accountNode = json.path("kakao_account");
            String email = accountNode.path("email").asText(null);
            String rawPhone = accountNode.path("phone_number").asText(null);
            String phone = formatPhoneNumber(rawPhone);
            String name = accountNode.path("profile").path("nickname").asText(null);

            return new KakaoUserInfo(kakaoId, email, name, phone);

        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 파싱 실패");
        }
    }

    private String formatPhoneNumber(String rawPhone) {
        if (rawPhone == null || !rawPhone.startsWith("+82")) {
            return rawPhone; // 변환 불가할 경우 원본 반환
        }

        // +82 10-1234-5678 → 010-1234-5678
        String formatted = rawPhone.replace("+82 ", "0");
        formatted = formatted.replaceAll("[^0-9]", ""); // 숫자만 남기기

        // 형식 맞춰서 하이픈 추가
        if (formatted.length() == 11) {
            return formatted.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        } else {
            return formatted; // 예상 형식이 아니면 그냥 반환
        }
    }
}
