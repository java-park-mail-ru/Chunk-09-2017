package application.unit;

import application.dao.user.UserDao;
import application.dao.user.UserDaoJpa;
import application.models.user.UserSignUp;
import application.models.user.UserUpdate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserDaoJpaTest implements UserDaoTest {

    @Autowired
    private UserDaoJpa userDao;

    protected UserSignUp sampleUser;
    protected UserSignUp wrongUser;
    protected UserSignUp getUser;
    protected UserUpdate updateUser;

    public UserDaoJpaTest() {
        sampleUser = new UserSignUp("testuser", "testemail","testpass");
        wrongUser = new UserSignUp("wronguser", "wrongemail","wrongpass");
        updateUser = new UserUpdate("newtestuser", "newtestemail",
                "testpass", "testpass");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Override
    @Before
    public void addSample() {
        sampleUser = getUserDao().addUser(sampleUser);
        assertNotNull(sampleUser.getId());
    }

    @Override
    @Test
    public void testCreateUser() {
        getUser = getUserDao().addUser(wrongUser);
        assertEquals(getUser, wrongUser);
    }

    @Override
    @Test
    public void testGetUserByLogin() {

        getUser = getUserDao().getUserByUsername(sampleUser.getUsername());
        assertNotNull(getUser);
        assertNotNull(getUser.getId());
        assertEquals(getUser, sampleUser);
    }

    @Override
    @Test
    public void testGetUserByEmail() {
        getUser = getUserDao().getUserByEmail(sampleUser.getEmail());
        assertNotNull(getUser);
        assertNotNull(getUser.getId());
        assertEquals(getUser, sampleUser);
    }

    @Override
    @Test
    public void testGetUserById() {
        getUser = getUserDao().getUserById(sampleUser.getId());
        assertNotNull(getUser);
        assertEquals(getUser.getId(), sampleUser.getId());
        assertEquals(getUser, sampleUser);
    }

    @Override
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

    @Override
    @Test
    public void testUpdateUser() {
        getUser = getUserDao().updateUser(updateUser, sampleUser.getId());
        assertNotNull(getUser);
        assertNotNull(getUser.getId());
        assertEquals(getUser.getId(), sampleUser.getId());
        assertEquals(getUser.getEmail(), updateUser.getEmail());
        assertEquals(getUser.getUsername(), updateUser.getUsername());
        assertEquals(getUser.getPassword(), updateUser.getPassword());
    }

    @Override
    @Test
    public void testGetUsersList() {
        getUserDao().addUser(new UserSignUp("test0", "test0", "pass"));
        final List<UserSignUp> addedUserList = Arrays.asList(
                new UserSignUp("test1", "test1", "pass"),
                new UserSignUp("test2", "test2", "pass"),
                new UserSignUp("test3", "test3", "pass"),
                new UserSignUp("test4", "test4", "pass"),
                new UserSignUp("test5", "test5", "pass")
        );
        addedUserList.forEach(user -> getUserDao().addUser(user));

        final List<UserSignUp> gettedUserList = getUserDao()
                .getUsers(addedUserList.size(), true);
        assertTrue(addedUserList.containsAll(gettedUserList));
    }

    protected UserDao getUserDao() {
        return this.userDao;
    }
}
