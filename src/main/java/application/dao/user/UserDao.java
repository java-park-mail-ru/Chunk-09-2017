package application.dao.user;

import application.models.UserModel;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserDao {

	UserModel addUser(UserModel userModel);

	UserModel updateUser(UserModel updateUser, Long id);

	UserModel getUserById(Long id);

	UserModel getUserByUsername(String username);

	UserModel getUserByEmail(String email);

	List<UserModel> getUsers(int limit, boolean asc);

	List<UserModel> getUsers(Integer limit);

	List<UserModel> getUsers();

	class UserDaoException extends RuntimeException {

		private String errorMessage;

		UserDaoException(String errorMessage, Throwable cause) {
			super(cause);
			this.errorMessage = errorMessage;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}

	class UserDaoExceptionDuplicateUsername extends UserDaoException {
		UserDaoExceptionDuplicateUsername(String username, Throwable cause) {
			super("User with username '" + username + "' is already exists", cause);
		}
	}

	class UserDaoExceptionDuplicateEmail extends UserDaoException {
		UserDaoExceptionDuplicateEmail(String email, Throwable cause) {
			super("User with email '" + email + "' is already exists", cause);
		}
	}
}
