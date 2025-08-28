package shympyo.auth.dto;

public record KakaoUserInfo(String socialId, String email, String name, String phone) implements SocialUserInfo {}
