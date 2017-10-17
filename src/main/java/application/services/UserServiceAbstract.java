package application.services;

import application.dao.user.UserDao;
import application.dao.user.UserDaoJpa;
import application.models.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public abstract class UserServiceAbstract {

	private static final int MIN_USERNAME_LENGTH = 4;
	private static final int MIN_PASSWORD_LENGTH = 4;

	protected UserDao userDao;

	public UserServiceAbstract(UserDao userDao) {
		this.userDao = userDao;
	}

	public Long addUser(UserModel userModel) {
		userValidation(userModel);
		return userDao.addUser(userModel).getId();
	}

//    public UserModel getUserById(Long id) {
//        return users.get(id);
//    }
//
//    public UserModel findUserByUsername(String username) {
//
//        for (UserModel user : users.values()) {
//            if (username.equals(user.getUsername())) {
//                return user;
//            }
//        }
//        return null;
//    }
//
//    public UserModel getUserByEmail(String email) {
//
//        for (UserModel user : users.values()) {
//            if (email.equals(user.getEmail())) {
//                return user;
//            }
//        }
//        return null;
//    }
//
//    public UserModel getUserByLogin(String login) {
//
//        for (UserModel user : users.values()) {
//            if (login.equals(user.getUsername()) || login.equals(user.getEmail())) {
//                return user;
//            }
//        }
//        return null;
//    }
//
	private void userValidation(UserModel user) {

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
					"The password must be longer than " +
							MIN_PASSWORD_LENGTH + " characters");
		}
		if (user.getUsername().length() < MIN_USERNAME_LENGTH) {
			throw new UserServiceExceptionIncorrectData(
					"The username must be longer than " +
							MIN_USERNAME_LENGTH + " characters");
		}
	}

	public abstract static class UserServiceException extends RuntimeException {
		protected String errorMessage;
		protected HttpStatus errorCode;

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
	}

	public static class UserServiceExceptionIncorrectData extends UserServiceException {

		public UserServiceExceptionIncorrectData(String errorMessage, Throwable cause) {
			super(errorMessage, HttpStatus.BAD_REQUEST, cause);
		}

		public UserServiceExceptionIncorrectData(String errorMessage) {
			super(errorMessage, HttpStatus.BAD_REQUEST);
		}

	}

}
