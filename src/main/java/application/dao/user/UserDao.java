package application.dao.user;

import application.models.UserModel;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserDao {

	UserModel addUser(UserModel userModel);

	UserModel updateUser(UserModel updateUser, Long id);

	UserModel getUserById(Long id);

	UserModel getUserByLogin(String login);

	UserModel getUserByEmail(String email);

	List<UserModel> getUsers(int limit, boolean asc);

	List<UserModel> getUsers(Integer limit);

	List<UserModel> getUsers();

	class TooShortPassword extends RuntimeException {
		public TooShortPassword(Integer minSymbol, Throwable cause) {
			super("Password is must be longer than " + minSymbol + " characters", cause);
		}
	}
}
