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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class UserDaoJpaTest {

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
        getUser = getUserDao().updateUser(updateUser, sampleUser.getId());
        assertNotNull(getUser);
        assertNotNull(getUser.getId());
        assertEquals(getUser.getId(), sampleUser.getId());
        assertEquals(getUser.getEmail(), updateUser.getEmail());
        assertEquals(getUser.getUsername(), updateUser.getUsername());
        assertEquals(getUser.getPassword(), updateUser.getPassword());
    }

    protected UserDao getUserDao() {
        return this.userDao;
    }
}
