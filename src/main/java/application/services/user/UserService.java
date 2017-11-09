package application.services.user;

import application.models.user.UserSignUp;
import application.models.user.UserUpdate;
import application.views.user.UserSuccess;

import javax.validation.constraints.NotNull;
import java.util.List;


public interface UserService {

    Long addUser(UserSignUp userSignUp);

    UserSignUp signInByLogin(String login, String password);

    UserSignUp updateUserProfile(UserUpdate newUser, @NotNull Long id);

    UserSignUp getUserById(@NotNull Long id);

    List<UserSignUp> getUserList();
}
