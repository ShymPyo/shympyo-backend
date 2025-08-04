package account_service.user.dto;

import account_service.user.domain.User;
import account_service.user.domain.UserRole;
import lombok.Getter;

@Getter
public class UserInfoResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String phone;
    private final UserRole role;

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.role = user.getRole();
    }
}