package application.exceptions.user;

import org.springframework.http.HttpStatus;

public class UserExceptionDuplicateUser extends UserException {

    public UserExceptionDuplicateUser(String username, String email, Throwable cause) {
        this(cause);
        String message = "";
        if (cause.getMessage() != null) {
            message += cause.getMessage();
        }
        if (message.contains("username")) {
            this.setErrorMessage("User with username '" + username + "' already exists");
        }
        if (message.contains("email")) {
            this.setErrorMessage("User with email '" + email + "' already exists");
        }
    }

    public UserExceptionDuplicateUser(String errorMessage, Throwable cause) {
        super(errorMessage, HttpStatus.CONFLICT, cause);
    }

    public UserExceptionDuplicateUser(Throwable cause) {
        super("Duplicate user entity in database", HttpStatus.CONFLICT, cause);
    }
}
