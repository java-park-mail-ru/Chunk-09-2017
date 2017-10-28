package application.dao.user;

import application.entities.UserEntity;
import application.models.UserSignUp;
import application.models.UserUpdate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Repository
public class UserDaoJpa implements UserDao {

    private EntityManager em;

    UserDaoJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public UserSignUp addUser(UserSignUp userSignUp) {
        final UserEntity userEntity = new UserEntity(userSignUp);
        em.persist(userEntity);
        userSignUp.setId(userEntity.getId());
        return userSignUp;
    }

    @Override
    @Nullable
    public UserSignUp updateUser(UserUpdate updateUser, Long id) {
        final UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            return null;
        }
        userEntity.update(updateUser);
        final UserSignUp updatedUsed = new UserSignUp(em.merge(userEntity));
        em.flush();
        return updatedUsed;
    }

    @Override
    public UserSignUp getUserByUsernameOrEmail(String login) {
        final TypedQuery<UserEntity> query = em.createQuery(
                "SELECT u FROM UserEntity u WHERE "
                        + "username = :username OR email = :email",
                UserEntity.class
        );
        query.setParameter("username", login);
        query.setParameter("email", login);
        return new UserSignUp(query.getSingleResult());
    }

    @Override
    public UserSignUp getUserByUsername(String username) {

        final TypedQuery<UserEntity> query = em.createQuery(
                "SELECT u FROM UserEntity u WHERE username = :username",
                UserEntity.class
        );
        query.setParameter("username", username);
        return new UserSignUp(query.getSingleResult());
    }

    @Override
    public UserSignUp getUserByEmail(String email) {

        final TypedQuery<UserEntity> query = em.createQuery(
                "SELECT u FROM UserEntity u WHERE email = :email",
                UserEntity.class
        );
        query.setParameter("email", email);
        return new UserSignUp(query.getSingleResult());
    }

    @Override
    @Nullable
    public UserSignUp getUserById(@NotNull Long id) {
        final UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            return null;
        } else {
            return new UserSignUp(userEntity);
        }
    }

    @Override
    public List<UserSignUp> getUsers(int limit, boolean desc) {
        final List<UserEntity> userEntityList = em.createQuery(
                "SELECT u FROM UserEntity u ORDER BY id " + (desc ? "DESC" : "ASC"),
                UserEntity.class
        ).setMaxResults(limit).getResultList();

        final List<UserSignUp> userSignUpList = new ArrayList<>(userEntityList.size());
        userEntityList.forEach(userEntity -> userSignUpList.add(new UserSignUp(userEntity)));

        return userSignUpList;
    }

    @Override
    public List<UserSignUp> getUsers(@Nullable Integer limit) {
        if (limit == null) {
            limit = DEFAULT_LIMIT;
        }
        return getUsers(limit, false);
    }

    @Override
    public List<UserSignUp> getUsers() {
        return getUsers(DEFAULT_LIMIT, false);
    }

    private static final Integer DEFAULT_LIMIT = 100;
}
