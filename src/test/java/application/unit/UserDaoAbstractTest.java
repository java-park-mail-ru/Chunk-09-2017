package application.unit;

import application.dao.user.UserDao;
import application.models.UserModel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public abstract class UserDaoAbstractTest {

	protected abstract UserDao getUserDao();

	protected UserModel sampleUser;
	protected UserModel wrongUser;
	protected UserModel getUser;

	UserDaoAbstractTest() {
		sampleUser = new UserModel("testuser", "testemail","testpass");
		wrongUser = new UserModel("wronguser", "wrongemail","wrongpass");
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void addSample() {
		sampleUser = getUserDao().addUser(sampleUser);
		assertNotNull(sampleUser.getId());
	}

	@Test
	public void testCreateUser() {
		getUser = getUserDao().addUser(wrongUser);
		assertEquals(getUser, wrongUser);
	}

	@Test
	public void testGetUserByLogin() {

		getUser = getUserDao().getUserByUsername(sampleUser.getUsername());
		assertNotNull(getUser);
		assertNotNull(getUser.getId());
		assertEquals(getUser, sampleUser);
	}

	@Test
	public void testGetUserByEmail() {
		getUser = getUserDao().getUserByEmail(sampleUser.getEmail());
		assertNotNull(getUser);
		assertNotNull(getUser.getId());
		assertEquals(getUser, sampleUser);
	}

	@Test
	public void testGetUserById() {
		getUser = getUserDao().getUserById(sampleUser.getId());
		assertNotNull(getUser);
		assertEquals(getUser.getId(), sampleUser.getId());
		assertEquals(getUser, sampleUser);
	}

	@Test
	public void testGetUserWrongById() {
		wrongUser = getUserDao().addUser(wrongUser);
		assertNotNull(wrongUser.getId());

		getUser = getUserDao().getUserById(sampleUser.getId());
		assertNotNull(getUser);
		assertNotNull(getUser.getId());
		assertNotEquals(getUser, wrongUser);
		assertEquals(getUser, sampleUser);

		getUser = getUserDao().getUserById(wrongUser.getId());
		assertNotNull(getUser);
		assertNotNull(getUser.getId());
		assertNotEquals(getUser, sampleUser);
		assertEquals(getUser, wrongUser);
	}

	@Test
	public void testUpdateUser() {
		getUser  = getUserDao().updateUser(wrongUser, sampleUser.getId());
		assertNotNull(getUser);
		assertNotNull(getUser.getId());
		assertEquals(getUser.getId(), sampleUser.getId());
		assertEquals(getUser, wrongUser);
	}

	@Test
	public void testGetUsersList() {
		getUserDao().addUser(new UserModel("test0", "test0", "pass"));
		final List<UserModel> addedUserList = Arrays.asList(
				new UserModel("test1", "test1", "pass"),
				new UserModel("test2", "test2", "pass"),
				new UserModel("test3", "test3", "pass"),
				new UserModel("test4", "test4", "pass"),
				new UserModel("test5", "test5", "pass")
		);
		addedUserList.forEach(user -> getUserDao().addUser(user));

		final List<UserModel> gettedUserList = getUserDao()
				.getUsers(addedUserList.size(), true);
		assertTrue(addedUserList.containsAll(gettedUserList));
	}
}
