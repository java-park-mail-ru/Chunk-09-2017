package application.dao.user;

import application.models.user.UserSignUp;
import application.models.user.UserUpdate;
import application.views.user.UserScore;
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

    List<UserScore> getScore(Integer offset, Integer pageSize);

    Long getNumberOfUsers();
}