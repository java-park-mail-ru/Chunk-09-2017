package application.services.user;

import application.exceptions.user.UserExceptionIncorrectData;
import application.models.UserUpdate;
import application.models.UserSignUp;


public class UserServiceTools {

    // Constants
    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int MIN_EMAIL_LENGTH = 4;

    private static final int MAX_USERNAME_LENGTH = 40;
    private static final int MAX_EMAIL_LENGTH = 50;

    // Validation
    public static void userValidationUpdate(UserUpdate user) throws UserExceptionIncorrectData {
        if (user.getPassword() != null
                && user.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new UserExceptionIncorrectData(
                    "The password must be longer than "
                            + MIN_PASSWORD_LENGTH + " characters");
        }
        if (user.getUsername() != null) {
            if (user.getUsername().length() < MIN_USERNAME_LENGTH) {
                throw new UserExceptionIncorrectData(
                        "The username must be longer than "
                                + MIN_USERNAME_LENGTH + " characters");
            }
            if (user.getUsername().length() > MAX_USERNAME_LENGTH) {
                throw new UserExceptionIncorrectData(
                        "The username must be shorter than "
                                + MAX_USERNAME_LENGTH + " characters");
            }
        }
        if (user.getEmail() != null) {
            if (user.getEmail().length() < MIN_EMAIL_LENGTH) {
                throw new UserExceptionIncorrectData(
                        "The email must be longer than "
                                + MIN_EMAIL_LENGTH + " characters");
            }
            if (user.getEmail().length() > MAX_EMAIL_LENGTH) {
                throw new UserExceptionIncorrectData(
                        "The email must be shorter than "
                                + MAX_EMAIL_LENGTH + " characters");
            }
        }
        if (user.getOldPassword() == null) {
            throw new UserExceptionIncorrectData(
                    "Enter the current password");
        }
    }

    public static void userValidation(UserSignUp user) throws UserExceptionIncorrectData {
        if (user.getPassword() == null) {
            throw new UserExceptionIncorrectData(
                    "The password field is missging");
        }
        if (user.getUsername() == null) {
            throw new UserExceptionIncorrectData(
                    "The username field is missging");
        }
        if (user.getEmail() == null) {
            throw new UserExceptionIncorrectData(
                    "The email field is missging");
        }
        if (user.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new UserExceptionIncorrectData(
                    "The password must be longer than "
                            + MIN_PASSWORD_LENGTH + " characters");
        }
        if (user.getUsername().length() < MIN_USERNAME_LENGTH) {
            throw new UserExceptionIncorrectData(
                    "The username must be longer than "
                            + MIN_USERNAME_LENGTH + " characters");
        }
        if (user.getUsername().length() > MAX_USERNAME_LENGTH) {
            throw new UserExceptionIncorrectData(
                    "The username must be shorter than "
                            + MAX_USERNAME_LENGTH + " characters");
        }
        if (user.getEmail().length() < MIN_EMAIL_LENGTH) {
            throw new UserExceptionIncorrectData(
                    "The email must be longer than "
                            + MIN_EMAIL_LENGTH + " characters");
        }
        if (user.getEmail().length() > MAX_EMAIL_LENGTH) {
            throw new UserExceptionIncorrectData(
                    "The email must be shorter than "
                            + MAX_EMAIL_LENGTH + " characters");
        }
    }
}
