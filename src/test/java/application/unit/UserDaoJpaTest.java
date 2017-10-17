package application.unit;

import application.dao.user.UserDao;
import application.dao.user.UserDaoJpa;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDaoJpaTest extends UserDaoAbstractTest {

	@Autowired
	private UserDaoJpa userDao;

	@Override
	protected UserDao getUserDao() {
		return this.userDao;
	}
}
