package shympyo.auth.dto;

public record GoogleUserInfo(String socialId, String email, String name, String phone) implements SocialUserInfo{
}
