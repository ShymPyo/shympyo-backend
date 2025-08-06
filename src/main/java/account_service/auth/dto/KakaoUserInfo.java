package account_service.auth.dto;

public record KakaoUserInfo(String email, Long kakaoId, String name, String phone) {}
