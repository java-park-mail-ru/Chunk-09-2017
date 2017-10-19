package application.services.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class UserServiceExceptions {

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
}
