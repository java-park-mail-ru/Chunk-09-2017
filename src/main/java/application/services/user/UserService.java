package application.services.user;

import application.models.user.UpdateUser;
import application.models.user.UserModel;

import javax.validation.constraints.NotNull;


public interface UserService {

    Long addUser(UserModel userModel);

    UserModel signInByLogin(String login, String password);

    UserModel updateUserProfile(UpdateUser newUser, @NotNull Long id);

    UserModel getUserById(@NotNull Long id);
}
