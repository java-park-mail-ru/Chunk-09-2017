package application.services.user;

import application.models.user.UserSignUp;
import application.models.user.UserUpdate;

import javax.validation.constraints.NotNull;


public interface UserService {

    Long addUser(UserSignUp userSignUp);

    UserSignUp signInByLogin(String login, String password);

    UserSignUp updateUserProfile(UserUpdate newUser, @NotNull Long id);

    UserSignUp getUserById(@NotNull Long id);
}
