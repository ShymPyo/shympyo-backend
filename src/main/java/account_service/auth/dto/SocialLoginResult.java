package account_service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class SocialLoginResult{

    private Long userId;
    private boolean isNewUser;
    private String accessToken;
    private String refreshToken;

    public SocialLoginResult(Long userId, boolean isNewUser) {
        this.isNewUser = isNewUser;
        this.userId = userId;
    }
}
