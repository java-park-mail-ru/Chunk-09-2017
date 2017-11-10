package application.dao.user;

import application.entities.UserEntity;
import application.models.user.UserSignUp;
import application.models.user.UserUpdate;
import application.views.user.UserScore;
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
    public List<UserScore> getScore(@NotNull Integer offset, @NotNull Integer pageSize) {

        final TypedQuery<UserEntity> query = em.createQuery(
                "SELECT u FROM UserEntity u", UserEntity.class);

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        final  List<UserEntity> userEntityList = query.getResultList();

        final List<UserScore> userScoreList = new ArrayList<>(userEntityList.size());
        userEntityList.forEach(userEntity -> userScoreList.add(new UserScore(userEntity)));
        return userScoreList;
    }

    @Override
    public Long getNumberOfUsers() {
        return (Long) em.createQuery(
                "SELECT COUNT(u.id) FROM UserEntity u")
                .getSingleResult();
    }
}
