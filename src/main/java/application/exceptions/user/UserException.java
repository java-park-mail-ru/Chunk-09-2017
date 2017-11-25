package application.exceptions.user;

import org.springframework.http.HttpStatus;


public abstract class UserException extends RuntimeException {

    private String errorMessage;
    private HttpStatus errorCode;

    UserException(String errorMessage, HttpStatus errorCode, Throwable cause) {
        super(cause);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    UserException(String errorMessage, HttpStatus errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }

    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    protected void setErrorCode(HttpStatus errorCode) {
        this.errorCode = errorCode;
    }
}
