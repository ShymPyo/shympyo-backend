package account_service.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class UpdateUserRequest {


    private String name;

    private String phone;

    public boolean isEmpty() {
        return name == null && phone == null;
    }

}
