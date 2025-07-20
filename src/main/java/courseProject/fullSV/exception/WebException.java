package courseProject.fullSV.exception;

import courseProject.fullSV.enums.ErrorCode;

import lombok.Getter;

@Getter
public class WebException extends RuntimeException{
    private ErrorCode errorCode;

    public WebException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
