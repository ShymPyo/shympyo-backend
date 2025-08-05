package account_service.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;

    public CommonResponse(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = null;
    }

}
