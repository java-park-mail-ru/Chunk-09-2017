package application.exceptions.user;

import org.springframework.http.HttpStatus;

public class UserExceptionUserIsNotExist extends UserException {

    public UserExceptionUserIsNotExist(String errorMessage, Throwable cause) {
        super(errorMessage, HttpStatus.NOT_FOUND, cause);
    }

    public UserExceptionUserIsNotExist(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }
}

