package account_service.global.exception;

import account_service.global.response.CommonResponse;
import account_service.global.response.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException e){
        log.error("[Error] 잘못된 값을 입력했습니다.", e);
        return ResponseEntity.badRequest()
                .body(ResponseUtil.fail(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<String>> handleGeneralException(Exception e) {
        log.error("[Error] 알 수 없는 예외가 발생했습니다.", e);
        return ResponseEntity.internalServerError()
                .body(ResponseUtil.fail("서버 내부 오류가 발생했습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

        log.warn("[ValidationError] {}", errorMsg);
        return ResponseEntity.badRequest().body(ResponseUtil.fail(errorMsg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Object>> handleJsonParse(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ResponseUtil.fail("요청 형식이 올바르지 않습니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseUtil.fail("접근 권한이 없습니다."));
    }
}
