package application.dao.user;


import application.entities.UserEntity;
import application.models.UserModel;
import org.springframework.stereotype.Component;


@Component
public interface UserDao {

	UserEntity createUser(UserModel userModel);

}
