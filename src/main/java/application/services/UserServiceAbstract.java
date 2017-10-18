package application.services;

import application.dao.user.UserDao;
import application.models.UpdateUser;
import application.models.UserModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Service
@Transactional
public abstract class UserServiceAbstract {

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int MIN_EMAIL_LENGTH = 4;

    private static final int MAX_USERNAME_LENGTH = 40;
    private static final int MAX_EMAIL_LENGTH = 50;

    private UserDao userDao;

    public UserServiceAbstract(UserDao userDao) {
        this.userDao = userDao;
    }

    public Long addUser(UserModel userModel) {
        try {
            userValidation(userModel);
            return userDao.addUser(userModel).getId();

        } catch (DataIntegrityViolationException e) {
            throw new UserServiceExceptionDuplicateUser(
                    userModel.getUsername(), userModel.getEmail(), e);
        }
    }

    public UserModel signInByLogin(String login, String password) {
        UserModel userModel;
        try {
            userModel = userDao.getUserByUsername(login);
        } catch (EmptyResultDataAccessException e1) {
            try {
                userModel = userDao.getUserByEmail(login);
            } catch (EmptyResultDataAccessException e2) {
                throw new UserServiceExceptionUserIsNotExist(
                        "Username with email/username '"
                                + login + "' does not exist", e2);
            }
        }
        if (!password.equals(userModel.getPassword())) {
            throw new UserServiceExceptionPasswordFail();
        }
        return userModel;
    }

    public UserModel updateUserProfile(UpdateUser newUser, @NotNull Long id) {
        try {
            userValidationUpdate(newUser);
            final UserModel oldUser = this.getUserById(id);
            if (!oldUser.getPassword().equals(newUser.getOldPassword())) {
                throw new UserServiceExceptionPasswordFail("Wrong password");
            }
            return userDao.updateUser(newUser, id);
        } catch (DataIntegrityViolationException e) {
            throw new UserServiceExceptionDuplicateUser(
                    newUser.getUsername(), newUser.getEmail(), e);
        }
    }

    public UserModel getUserById(@NotNull Long id) {
        final UserModel user = userDao.getUserById(id);
        if (user == null) {
            throw new UserServiceExceptionUserIsNotExist("Your session expired");
        }
        return user;
    }

    private void userValidationUpdate(UpdateUser user) throws UserServiceExceptionIncorrectData {
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

    private void userValidation(UserModel user) throws UserServiceExceptionIncorrectData {
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

    // UserServiceException
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
