package application.services.user;

import application.models.UserUpdate;
import application.models.UserSignUp;

import javax.validation.constraints.NotNull;


public interface UserService {

    Long addUser(UserSignUp userSignUp);

    UserSignUp signInByLogin(String login, String password);

    UserSignUp updateUserProfile(UserUpdate newUser, @NotNull Long id);

    UserSignUp getUserById(@NotNull Long id);
}
