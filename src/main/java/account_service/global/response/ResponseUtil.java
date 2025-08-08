package account_service.global.response;

public class ResponseUtil {

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, 200, "요청이 성공했습니다.", data);
    }

    public static <T> CommonResponse<T> success(String message, T data){
        return new CommonResponse<>(true, 200, message, data);
    }

    public static <T> CommonResponse<T> success(String message){
        return new CommonResponse<>(true, 200, message);
    }

    public static <T> CommonResponse<T> fail(int code, String message) {
        return new CommonResponse<>(false, code, message, null);
    }

    public static <T> CommonResponse<T> fail( String message) {
        return new CommonResponse<>(false, 400, message, null);
    }
}
