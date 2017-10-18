package application.dao.user;

import application.entities.UserEntity;
import application.models.UserModel;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Repository
@Transactional
public class UserDaoJpa implements UserDao {

    private EntityManager em;

    UserDaoJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public UserModel addUser(UserModel userModel) {
        final UserEntity userEntity = new UserEntity(userModel);
        em.persist(userEntity);
        userModel.setId(userEntity.getId());
        return userModel;
    }

    @Override
    @Nullable
    public UserModel updateUser(UserModel updateUser, Long id) {
        final UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            return null;
        }
        userEntity.update(updateUser);
        return new UserModel(em.merge(userEntity));
    }

    @Override
    public UserModel getUserByUsername(String username) {

        final TypedQuery<UserEntity> query = em.createQuery(
                "SELECT u FROM UserEntity u WHERE username = :username",
                UserEntity.class
        );
        query.setParameter("username", username);
        return new UserModel(query.getSingleResult());
    }

    @Override
    public UserModel getUserByEmail(String email) {

        final TypedQuery<UserEntity> query = em.createQuery(
                "SELECT u FROM UserEntity u WHERE email = :email",
                UserEntity.class
        );
        query.setParameter("email", email);
        return new UserModel(query.getSingleResult());
    }

    @Override
    @Nullable
    public UserModel getUserById(@NotNull Long id) {
        final UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            return null;
        } else {
            return new UserModel(userEntity);
        }
    }

    @Override
    public List<UserModel> getUsers(int limit, boolean desc) {
        final List<UserEntity> userEntityList = em.createQuery(
                "SELECT u FROM UserEntity u ORDER BY id " + (desc ? "DESC" : "ASC"),
                UserEntity.class
        ).setMaxResults(limit).getResultList();

        final List<UserModel> userModelList = new ArrayList<>(userEntityList.size());
        userEntityList.forEach(userEntity -> userModelList.add(new UserModel(userEntity)));

        return userModelList;
    }

    @Override
    public List<UserModel> getUsers(@Nullable Integer limit) {
        if (limit == null) {
            limit = DEFAULT_LIMIT;
        }
        return getUsers(limit, false);
    }

    @Override
    public List<UserModel> getUsers() {
        return getUsers(DEFAULT_LIMIT, false);
    }

    private static final Integer DEFAULT_LIMIT = 100;
}
