package application.services.user;

import application.dao.user.UserDao;
import application.dao.user.UserDaoJpa;
import application.exceptions.user.UserExceptionDuplicateUser;
import application.exceptions.user.UserExceptionPasswordFail;
import application.exceptions.user.UserExceptionUserIsNotExist;
import application.models.UpdateUser;
import application.models.UserModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;


@Service
@Transactional
public class UserServiceJpa implements UserService {

    private UserDao userDao;

    public UserServiceJpa(UserDaoJpa userDao) {
        this.userDao = userDao;
    }

    @Override
    public Long addUser(UserModel userModel) {
        try {
            UserServiceTools.userValidation(userModel);
            return userDao.addUser(userModel).getId();

        } catch (DataIntegrityViolationException e) {
            throw new UserExceptionDuplicateUser(
                    userModel.getUsername(), userModel.getEmail(), e);
        }
    }

    @Override
    public UserModel signInByLogin(String login, String password) {
        final UserModel userModel;
        try {
            userModel = userDao.getUserByUsernameOrEmail(login);
        } catch (EmptyResultDataAccessException e) {
            throw new UserExceptionUserIsNotExist(
                    "Username with email/username '"
                            + login + "' does not exist", e);
        }
        if (!password.equals(userModel.getPassword())) {
            throw new UserExceptionPasswordFail();
        }
        return userModel;
    }

    @Override
    public UserModel updateUserProfile(UpdateUser newUser, @NotNull Long id) {
        try {
            UserServiceTools.userValidationUpdate(newUser);
            final UserModel oldUser = this.getUserById(id);
            if (!oldUser.getPassword().equals(newUser.getOldPassword())) {
                throw new UserExceptionPasswordFail("Wrong password");
            }
            return userDao.updateUser(newUser, id);
        } catch (DataIntegrityViolationException e) {
            throw new UserExceptionDuplicateUser(
                    newUser.getUsername(), newUser.getEmail(), e);
        }
    }

    @Override
    public UserModel getUserById(@NotNull Long id) {
        final UserModel user = userDao.getUserById(id);
        if (user == null) {
            throw new UserExceptionUserIsNotExist("Your session expired");
        }
        return user;
    }
}
