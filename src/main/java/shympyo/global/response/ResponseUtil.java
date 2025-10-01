package shympyo.global.response;

import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<CommonResponse<T>> success(T data) {

        return ResponseEntity.ok(new CommonResponse<>(true, 200, "요청이 성공했습니다.", data));

    }

    public static <T> ResponseEntity<CommonResponse<T>> success(String message, T data){

        return ResponseEntity.ok(new CommonResponse<>(true, 200, message, data));

    }

    public static <T> ResponseEntity<CommonResponse> success(String message){

        return ResponseEntity.ok(new CommonResponse(true, 200, message));

    }

    public static <T> ResponseEntity<CommonResponse<T>> fail(int code, String message) {

        CommonResponse<T> response = new CommonResponse<>(false, code, message, null);

        return ResponseEntity.status(code).body(response);
    }

    public static <T> ResponseEntity<CommonResponse<T>> fail( String message) {

        CommonResponse<T> response = new CommonResponse<>(false, 400, message, null);

        return ResponseEntity.status(400).body(response);
    }
}
