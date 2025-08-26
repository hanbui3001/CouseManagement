package courseProject.fullSV.exception;

import courseProject.fullSV.dto.response.ErrorResponse;
import courseProject.fullSV.enums.ErrorCode;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(WebException.class)
    public ResponseEntity<ErrorResponse> webExceptionHandler(WebException exception){
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentExceptionHandler(MethodArgumentNotValidException exception){
        String errorKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode ;
        var error = exception.getBindingResult().getAllErrors().getFirst();
        Map<String, Object> attributes = new HashMap<>();
        try {
            errorCode = ErrorCode.valueOf(errorKey);
            if(error.contains(ConstraintViolation.class)){
                var violator = error.unwrap(ConstraintViolation.class);
                attributes = violator.getConstraintDescriptor().getAttributes();
                log.warn(attributes.toString());
            }
        }catch (IllegalArgumentException e){
            log.warn("key invalid");
            errorCode = ErrorCode.KEY_INVALID;
        }
        String message = mapAttribute(errorCode.getMessage(), attributes);
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), message);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
    private String mapAttribute(String message, Map<String, Object> attributes ) {
        for(Map.Entry<String, Object> entry : attributes.entrySet()){
            String key ="{" + entry.getKey() + "}";
            message = message.replace( key , String.valueOf(entry.getValue()));
        }
        return message;
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> dataIntegrityExceptionHandler(DataIntegrityViolationException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ErrorCode.USER_EXISTED.getCode(), ErrorCode.USER_EXISTED.getMessage()));
    }
}
