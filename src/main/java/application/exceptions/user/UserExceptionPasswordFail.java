package application.exceptions.user;

import org.springframework.http.HttpStatus;

public class UserExceptionPasswordFail extends UserException {

    public UserExceptionPasswordFail() {
        super("Incorrect password or login", HttpStatus.FORBIDDEN);
    }

    public UserExceptionPasswordFail(String errorMessage) {
        super(errorMessage, HttpStatus.FORBIDDEN);
    }
}
