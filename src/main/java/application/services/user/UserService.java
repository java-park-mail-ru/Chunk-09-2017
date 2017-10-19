package application.services.user;

import application.models.UpdateUser;
import application.models.UserModel;

import javax.validation.constraints.NotNull;


public interface UserService {

    Long addUser(UserModel userModel);

    UserModel signInByLogin(String login, String password);

    UserModel updateUserProfile(UpdateUser newUser, @NotNull Long id);

    UserModel getUserById(@NotNull Long id);
}
