package account_service.auth.dto;

public record NaverUserInfo(String socialId, String email, String name, String phone) implements SocialUserInfo {
}
