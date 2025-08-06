package account_service.auth.service;

import account_service.auth.domain.RefreshToken;
import account_service.auth.dto.GoogleUserInfo;
import account_service.auth.dto.SocialLoginResult;
import account_service.auth.dto.TokenResponse;
import account_service.auth.jwt.JwtTokenProvider;
import account_service.auth.repository.RefreshTokenRepository;
import account_service.user.domain.UserRole;
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
public class GoogleOAuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.token-uri}")
    private String tokenUri;

    @Value("${google.user-info-uri}")
    private String userInfoUri;

    public SocialLoginResult googleLogin(String code) {
        String GoogleAccessToken = getAccessToken(code);
        GoogleUserInfo userInfo = getUserInfo(GoogleAccessToken);

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
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<?> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("구글 토큰 파싱 실패", e);
        }
    }

    private GoogleUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());

            String email = json.get("email").asText(null);
            String googleID = json.get("id").asText(null);
            String givenName = json.path("given_name").asText(""); // 예: 길동
            String familyName = json.path("family_name").asText(""); // 예: 홍
            String fullName = familyName + givenName;
            String phone = getPhoneNumberFromGoogle(accessToken);
            return new GoogleUserInfo(googleID, email, fullName, phone);

        } catch (Exception e) {
            throw new RuntimeException("구글 사용자 정보 파싱 실패", e);
        }
    }

    private String getPhoneNumberFromGoogle(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://people.googleapis.com/v1/people/me?personFields=phoneNumbers",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode json = objectMapper.readTree(response.getBody());
            JsonNode phoneNumbers = json.path("phoneNumbers");

            if (phoneNumbers.isArray() && phoneNumbers.size() > 0) {
                return phoneNumbers.get(0).path("value").asText(); // E.164 형식 예: +82 10-1234-5678
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("구글 전화번호 파싱 실패", e);
        }
    }

}

