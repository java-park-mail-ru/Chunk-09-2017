package application.functional;

import application.models.SignInModel;
import application.models.UpdateUser;
import application.models.UserModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Rule
    public ExpectedException expected = ExpectedException.none();


    // SignUp
    @Test
    public void testSignUpUserSuccess() throws Exception {
        final UserModel testUser = getRandomUser();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value(testUser.getUsername()))
                .andExpect(jsonPath("email").value(testUser.getEmail()))
                .andExpect(jsonPath("password").doesNotExist()).andDo(print());
        assertNotNull(mockHttpSession.getAttribute("ID"));
    }

    @Test
    public void testSignUpUserNotEnouthFields() throws Exception {
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpUserIncorrectFields() throws Exception {
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel("", "", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpUserWrongMediaType() throws Exception {
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testSignUpUserDuplicate() throws Exception {
        final UserModel userModel = new UserModel("testName", "testEmail", "pass");
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userModel)))
                .andExpect(status().isCreated());
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userModel)))
                .andExpect(status().isConflict());

    }

    @Test
    public void testSignUpUserTooLongFields() throws Exception {
        final UserModel testUser = getRandomUser();
        testUser.setUsername("veryLoooooooooooooooooooooooooooooooooooooooooooooooongTestName");
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andExpect(status().isBadRequest());
    }

    // SignIn
    @Test
    public void testSignInEmailUserSuccess() throws Exception {
        final MockHttpSession mockHttpSession = new MockHttpSession();
        final UserModel testUser = getRandomUser();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andDo(print());

        mockMvc.perform(get(getBaseUrl() + "/exit")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID")))
                .andExpect(status().isOk());

        mockMvc.perform(post(getBaseUrl() + "/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new SignInModel(
                        testUser.getEmail(),
                        testUser.getPassword()
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(testUser.getEmail()))
                .andExpect(jsonPath("username").value(testUser.getPassword()))
                .andDo(print());

//        assertTrue(false);
    }

    @Test
    public void testSignInUsernameUserSuccess() throws Exception {
        final UserModel testUser = getRandomUser();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value(testUser.getUsername()))
                .andExpect(jsonPath("email").value(testUser.getEmail()))
                .andExpect(jsonPath("password").doesNotExist()).andDo(print());

        mockMvc.perform(post(getBaseUrl() + "/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new SignInModel(
                        testUser.getUsername(),
                        testUser.getPassword())
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(testUser.getUsername()))
                .andExpect(jsonPath("email").value(testUser.getEmail()))
                .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void testSignInWrongPassword() throws Exception {
        final UserModel testUser = getRandomUser();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value(testUser.getUsername()))
                .andExpect(jsonPath("email").value(testUser.getEmail()))
                .andExpect(jsonPath("password").doesNotExist()).andDo(print());


        mockMvc.perform(post(getBaseUrl() + "/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new SignInModel(
                        testUser.getUsername(),
                        "wrongPassword")
                )))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testSignInDoesNotExist() throws Exception {
        mockMvc.perform(post(getBaseUrl() + "/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new SignInModel(
                        "isNotExist",
                        "pass")
                )))
                .andExpect(status().isNotFound());
    }

    // Update
    @Test
    public void testUpdateSuccess() throws Exception {
        final UserModel oldUser = getRandomUser();
        final UserModel newUser = getRandomUser();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(oldUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value(oldUser.getUsername()))
                .andExpect(jsonPath("email").value(oldUser.getEmail()))
                .andExpect(jsonPath("password").doesNotExist());

        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        newUser.getUsername(),
                        newUser.getEmail(),
                        newUser.getPassword(),
                        oldUser.getPassword()
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(newUser.getUsername()))
                .andExpect(jsonPath("email").value(newUser.getEmail()))
                .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void testUpdateWrongSessionId() throws Exception {
        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        null, null, null, "pass"
                ))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateIncorrectFieldsTooLong() throws Exception {
        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        "LooooooooooooooooooooooooooooooooooooooooooooooongName",
                        "pass", null, "pass")
                )))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateNullParametrs() throws Exception {
        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        null, null, null, null
                ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateConflict() throws Exception {
        final UserModel userOne = getRandomUser();
        final UserModel userTwo = getRandomUser();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userOne)))
                .andExpect(status().isCreated());

        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userTwo)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        userOne.getUsername(),
                        userOne.getEmail(),
                        userTwo.getPassword(),
                        userTwo.getPassword()
                ))))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateUnauthorized() throws Exception {
        mockMvc.perform(post(getBaseUrl() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        "testName",
                        "testEmail",
                        "pass",
                        "pass"
                ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateWrongPasswordForbidden() throws Exception {
        final UserModel testUser = getRandomUser();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        testUser.getUsername(),
                        testUser.getEmail(),
                        testUser.getPassword(),
                        "wrongPassword"
                ))))
                .andExpect(status().isForbidden());
    }

    // Session
    @Test
    public void testSessionWrongSessionId() throws Exception {
        mockMvc.perform(get(getBaseUrl() + "/whoisit")
                .sessionAttr("ID", -1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSessionRightSessionId() throws Exception {
        final UserModel testUser = getRandomUser();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(get(getBaseUrl() + "/whoisit")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(testUser.getUsername()))
                .andExpect(jsonPath("email").value(testUser.getEmail()))
                .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void testSessionEmptySessionId() throws Exception {
                mockMvc.perform(get(getBaseUrl() + "/whoisit"))
                .andExpect(status().isUnauthorized());
    }



    // Exit
    @Test
    public void testExitWrongSessionId() throws Exception {
        mockMvc.perform(get(getBaseUrl() + "/exit")
                .sessionAttr("ID", -1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testExitRightSessionId() throws Exception {
        final UserModel testUser = getRandomUser();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(get(getBaseUrl() + "/exit")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID")))
                .andExpect(status().isOk());
    }


    private final Random random = new Random(new Date().getTime());

    private final ObjectMapper mapper = new ObjectMapper();

    private String getBaseUrl() {
        return "/user";
    }

    private UserModel getRandomUser() {
        return new UserModel(
                "TestUsername_" + random.nextGaussian(),
                "TestEmail_" + random.nextGaussian(),
                "TestPassword_" + random.nextGaussian()
        );
    }

    private String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }
}
