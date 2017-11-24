package application.exceptions.user;

import org.springframework.http.HttpStatus;


public final class UserExceptionIncorrectData extends UserException {

    public UserExceptionIncorrectData(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}

