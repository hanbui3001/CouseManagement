package courseProject.fullSV.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "user existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1003, "unauthenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1004, "user have not permission", HttpStatus.FORBIDDEN),
    USERNAME_NOT_VALID(1005, "username must be at least 3 character", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_VALID(1006, "password must be at least 3 character", HttpStatus.BAD_REQUEST),
    KEY_INVALID(1007, "Key validation is invalid", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatus status;

}
