package application.services.user;

import application.models.UpdateUser;
import application.models.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class UserServiceExceptions {

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int MIN_EMAIL_LENGTH = 4;

    private static final int MAX_USERNAME_LENGTH = 40;
    private static final int MAX_EMAIL_LENGTH = 50;


    public abstract static class UserServiceException extends RuntimeException {
        private String errorMessage;
        private HttpStatus errorCode;

        UserServiceException(String errorMessage, HttpStatus errorCode, Throwable cause) {
            super(cause);
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }

        UserServiceException(String errorMessage, HttpStatus errorCode) {
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

    public static class UserServiceExceptionIncorrectData extends UserServiceException {

        public UserServiceExceptionIncorrectData(String errorMessage) {
            super(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    public static class UserServiceExceptionDuplicateUser extends UserServiceException {

        public UserServiceExceptionDuplicateUser(String username, String email, Throwable cause) {
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

        public UserServiceExceptionDuplicateUser(String errorMessage, Throwable cause) {
            super(errorMessage, HttpStatus.CONFLICT, cause);
        }

        public UserServiceExceptionDuplicateUser(Throwable cause) {
            super("Duplicate user entity in database", HttpStatus.CONFLICT, cause);
        }
    }

    public static class UserServiceExceptionUserIsNotExist extends UserServiceException {

        public UserServiceExceptionUserIsNotExist(String errorMessage, Throwable cause) {
            super(errorMessage, HttpStatus.NOT_FOUND, cause);
        }

        public UserServiceExceptionUserIsNotExist(String errorMessage) {
            super(errorMessage, HttpStatus.NOT_FOUND);
        }
    }

    public static class UserServiceExceptionPasswordFail extends UserServiceException {

        public UserServiceExceptionPasswordFail() {
            super("Incorrect password or login", HttpStatus.FORBIDDEN);
        }

        public UserServiceExceptionPasswordFail(String errorMessage) {
            super(errorMessage, HttpStatus.FORBIDDEN);
        }
    }

    // Validation
    public static void userValidationUpdate(UpdateUser user) throws UserServiceExceptionIncorrectData {
        if (user.getPassword() != null) {
            if (user.getPassword().length() < MIN_PASSWORD_LENGTH) {
                throw new UserServiceExceptionIncorrectData(
                        "The password must be longer than "
                                + MIN_PASSWORD_LENGTH + " characters");
            }
        }
        if (user.getUsername() != null) {
            if (user.getUsername().length() < MIN_USERNAME_LENGTH) {
                throw new UserServiceExceptionIncorrectData(
                        "The username must be longer than "
                                + MIN_USERNAME_LENGTH + " characters");
            }
            if (user.getUsername().length() > MAX_USERNAME_LENGTH) {
                throw new UserServiceExceptionIncorrectData(
                        "The username must be shorter than "
                                + MAX_USERNAME_LENGTH + " characters");
            }
        }
        if (user.getEmail() != null) {
            if (user.getEmail().length() < MIN_EMAIL_LENGTH) {
                throw new UserServiceExceptionIncorrectData(
                        "The email must be longer than "
                                + MIN_EMAIL_LENGTH + " characters");
            }
            if (user.getEmail().length() > MAX_EMAIL_LENGTH) {
                throw new UserServiceExceptionIncorrectData(
                        "The email must be shorter than "
                                + MAX_EMAIL_LENGTH + " characters");
            }
        }
        if (user.getOldPassword() == null) {
            throw new UserServiceExceptionIncorrectData(
                    "Enter the current password");
        }
    }

    public static void userValidation(UserModel user) throws UserServiceExceptionIncorrectData {
        if (user.getPassword() == null) {
            throw new UserServiceExceptionIncorrectData(
                    "The password field is missging");
        }
        if (user.getUsername() == null) {
            throw new UserServiceExceptionIncorrectData(
                    "The username field is missging");
        }
        if (user.getEmail() == null) {
            throw new UserServiceExceptionIncorrectData(
                    "The email field is missging");
        }
        if (user.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new UserServiceExceptionIncorrectData(
                    "The password must be longer than "
                            + MIN_PASSWORD_LENGTH + " characters");
        }
        if (user.getUsername().length() < MIN_USERNAME_LENGTH) {
            throw new UserServiceExceptionIncorrectData(
                    "The username must be longer than "
                            + MIN_USERNAME_LENGTH + " characters");
        }
        if (user.getUsername().length() > MAX_USERNAME_LENGTH) {
            throw new UserServiceExceptionIncorrectData(
                    "The username must be shorter than "
                            + MAX_USERNAME_LENGTH + " characters");
        }
        if (user.getEmail().length() < MIN_EMAIL_LENGTH) {
            throw new UserServiceExceptionIncorrectData(
                    "The email must be longer than "
                            + MIN_EMAIL_LENGTH + " characters");
        }
        if (user.getEmail().length() > MAX_EMAIL_LENGTH) {
            throw new UserServiceExceptionIncorrectData(
                    "The email must be shorter than "
                            + MAX_EMAIL_LENGTH + " characters");
        }
    }
}
