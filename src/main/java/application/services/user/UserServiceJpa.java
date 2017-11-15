package application.services.user;

import application.dao.user.UserDao;
import application.dao.user.UserDaoJpa;
import application.exceptions.user.UserExceptionDuplicateUser;
import application.exceptions.user.UserExceptionPasswordFail;
import application.exceptions.user.UserExceptionUserIsNotExist;
import application.models.user.UserUpdate;
import application.models.user.UserSignUp;
import application.views.game.ScoreTable;

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
    public Long addUser(UserSignUp userSignUp) {
        try {
            UserTools.userValidation(userSignUp);
            return userDao.addUser(userSignUp).getId();

        } catch (DataIntegrityViolationException e) {
            throw new UserExceptionDuplicateUser(
                    userSignUp.getUsername(), userSignUp.getEmail(), e);
        }
    }

    @Override
    public UserSignUp signInByLogin(String login, String password) {
        final UserSignUp userSignUp;
        try {
            userSignUp = userDao.getUserByUsernameOrEmail(login);
        } catch (EmptyResultDataAccessException e) {
            throw new UserExceptionUserIsNotExist(
                    "Incorrect password or login", e);

        }
        if (!password.equals(userSignUp.getPassword())) {
            throw new UserExceptionPasswordFail();
        }
        return userSignUp;
    }

    @Override
    public UserSignUp updateUserProfile(UserUpdate newUser, @NotNull Long id) {
        try {
            UserTools.userValidationUpdate(newUser);
            final UserSignUp oldUser = this.getUserById(id);
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
    public UserSignUp getUserById(@NotNull Long id) {
        final UserSignUp user = userDao.getUserById(id);
        if (user == null) {
            throw new UserExceptionUserIsNotExist("Your session expired");
        }
        return user;
    }

    @Override
    public ScoreTable getScoreTable(Integer offset, Integer pageSize) {
        return new ScoreTable(
                userDao.getScore(offset, pageSize),
                userDao.getNumberOfUsers()
        );
    }
}
