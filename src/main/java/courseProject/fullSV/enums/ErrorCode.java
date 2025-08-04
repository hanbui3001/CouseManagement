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
    REFRESH_TOKEN_EXPIRED(1008, "refresh token is expired", HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_NOT_FOUND(1009, "refresh token is not in database", HttpStatus.FORBIDDEN),
    COURSE_NOT_FOUND(1100, "course not found", HttpStatus.NOT_FOUND),
    ALREADY_ENROLLED(1101, "this course already enrolled", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1102, "role not found", HttpStatus.NOT_FOUND),
    ;
    private int code;
    private String message;
    private HttpStatus status;

}
