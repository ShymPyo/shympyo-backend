package account_service.auth.dto;

public record GoogleUserInfo(String email, String googleId, String name, String phone) {
}
