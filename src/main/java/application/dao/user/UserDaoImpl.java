package application.dao.user;

import application.entities.UserEntity;
import application.models.UserModel;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


@Repository
@Transactional
public class UserDaoImpl implements UserDao {

	@PersistenceContext
	private EntityManager em;

	UserDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public UserEntity createUser(UserModel userModel) {
		final UserEntity userEntity = new UserEntity(userModel);
		em.persist(userEntity);
		return userEntity;
	}
}
