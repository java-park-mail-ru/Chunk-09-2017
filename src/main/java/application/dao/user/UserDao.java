package application.dao.user;

import application.models.UserModel;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@SuppressWarnings("unused")
public interface UserDao {

    UserModel addUser(UserModel userModel);

    UserModel updateUser(UserModel updateUser, Long id);

    UserModel getUserById(Long id);

    UserModel getUserByUsername(String username);

    UserModel getUserByEmail(String email);

    List<UserModel> getUsers(int limit, boolean asc);

    List<UserModel> getUsers(Integer limit);

    List<UserModel> getUsers();
}
