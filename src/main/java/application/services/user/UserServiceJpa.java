package application.services.user;

import application.dao.user.UserDao;
import application.dao.user.UserDaoJpa;
import application.models.UpdateUser;
import application.models.UserModel;
import application.services.user.UserServiceExceptions.*;
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
            throw new UserServiceExceptionDuplicateUser(
                    userModel.getUsername(), userModel.getEmail(), e);
        }
    }

    @Override
    public UserModel signInByLogin(String login, String password) {
        UserModel userModel;
        try {
            userModel = userDao.getUserByUsername(login);
        } catch (EmptyResultDataAccessException e1) {
            try {
                userModel = userDao.getUserByEmail(login);
            } catch (EmptyResultDataAccessException e2) {
                throw new UserServiceExceptionUserIsNotExist(
                        "Username with email/username '"
                                + login + "' does not exist", e2);
            }
        }
        if (!password.equals(userModel.getPassword())) {
            throw new UserServiceExceptionPasswordFail();
        }
        return userModel;
    }

    @Override
    public UserModel updateUserProfile(UpdateUser newUser, @NotNull Long id) {
        try {
            UserServiceTools.userValidationUpdate(newUser);
            final UserModel oldUser = this.getUserById(id);
            if (!oldUser.getPassword().equals(newUser.getOldPassword())) {
                throw new UserServiceExceptionPasswordFail("Wrong password");
            }
            return userDao.updateUser(newUser, id);
        } catch (DataIntegrityViolationException e) {
            throw new UserServiceExceptionDuplicateUser(
                    newUser.getUsername(), newUser.getEmail(), e);
        }
    }

    @Override
    public UserModel getUserById(@NotNull Long id) {
        final UserModel user = userDao.getUserById(id);
        if (user == null) {
            throw new UserServiceExceptionUserIsNotExist("Your session expired");
        }
        return user;
    }
}
