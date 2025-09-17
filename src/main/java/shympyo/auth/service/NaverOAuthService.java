package shympyo.auth.service;

import shympyo.auth.domain.RefreshToken;
import shympyo.auth.dto.SocialLoginResult;
import shympyo.auth.jwt.JwtTokenProvider;
import shympyo.auth.repository.RefreshTokenRepository;
import shympyo.user.domain.UserRole;
import shympyo.auth.dto.NaverUserInfo;
import shympyo.user.service.UserService;
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
public class NaverOAuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    @Value("${naver.client-secret}")
    private String clientSecret;


    public SocialLoginResult naverLogin(String code) {
        String NaverAccessToken = getAccessToken(code);
        NaverUserInfo userInfo = getUserInfo(NaverAccessToken);

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
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);


        HttpEntity<?> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://nid.naver.com/oauth2.0/token", request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("네이버 토큰 파싱 실패");
        }
    }

    private NaverUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request,
                String.class
        );

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            JsonNode responseNode = json.get("response");
            String naverId = responseNode.get("id").asText(); // 보통 String으로 받는 게 맞음

            String email = responseNode.path("email").asText(null);
            String phone = responseNode.path("mobile").asText(null);
            String name = responseNode.path("name").asText(null);


            return new NaverUserInfo(naverId, email, name, phone);

        } catch (Exception e) {
            throw new RuntimeException("네이버 사용자 정보 파싱 실패");
        }
    }
}
