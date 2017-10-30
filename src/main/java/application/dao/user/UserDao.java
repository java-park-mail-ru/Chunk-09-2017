package application.dao.user;

import application.models.UserSignUp;
import application.models.UserUpdate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@SuppressWarnings("unused")
public interface UserDao {

    UserSignUp addUser(UserSignUp userSignUp);

    UserSignUp updateUser(UserUpdate updateUser, Long id);

    UserSignUp getUserById(Long id);

    UserSignUp getUserByUsername(String username);

    UserSignUp getUserByEmail(String email);

    UserSignUp getUserByUsernameOrEmail(String login);

    List<UserSignUp> getUsers(int limit, boolean asc);

    List<UserSignUp> getUsers(Integer limit);

    List<UserSignUp> getUsers();
}
