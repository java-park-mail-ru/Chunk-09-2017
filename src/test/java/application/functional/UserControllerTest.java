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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    // SignUp
    @Test
    public void testSignUpUserSuccess() throws Exception {
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel(
                        "testName",
                        "testEmail",
                        "pass")
                ))).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value("testName"))
                .andExpect(jsonPath("email").value("testEmail"))
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
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel(
                        "veryLoooooooooooooooooooooooooooooooooooooooooooooooongTestName",
                        "testEmail",
                        "pass")
                )))
                .andExpect(status().isBadRequest());
    }

    // SignIn
    @Test
    public void testSignInEmailUserSuccess() throws Exception {
        testSignUpUserSuccess();
        mockMvc.perform(post(getBaseUrl() + "/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new SignInModel(
                        "testEmail",
                        "pass")
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("testEmail"))
                .andExpect(jsonPath("username").value("testName"))
                .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void testSignInUsernameUserSuccess() throws Exception {
        testSignUpUserSuccess();
        mockMvc.perform(post(getBaseUrl() + "/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new SignInModel(
                        "testName",
                        "pass")
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("testEmail"))
                .andExpect(jsonPath("username").value("testName"))
                .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void testSignInWrongPassword() throws Exception {
        testSignUpUserSuccess();
        mockMvc.perform(post(getBaseUrl() + "/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new SignInModel(
                        "testName",
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
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel(
                        "testName",
                        "testEmail",
                        "pass"
                ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value("testName"))
                .andExpect(jsonPath("email").value("testEmail"))
                .andExpect(jsonPath("password").doesNotExist());

        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        "newTestName",
                        "newTestEmail",
                        "newPass",
                        "pass"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("newTestName"))
                .andExpect(jsonPath("email").value("newTestEmail"))
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
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel(
                        "testName",
                        "testEmail",
                        "pass"
                )))).andExpect(status().isCreated());

        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel(
                        "testName2",
                        "testEmail2",
                        "pass"
                )))).andExpect(status().isCreated());

        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        "testName",
                        "testEmail",
                        "pass",
                        "pass"
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
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel(
                        "testName",
                        "testEmail",
                        "pass"
                )))).andExpect(status().isCreated());

        mockMvc.perform(post(getBaseUrl() + "/update")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UpdateUser(
                        "testNameNew",
                        "testEmailNew",
                        "passNew",
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

        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel(
                        "testName",
                        "testEmail",
                        "pass"
                )))).andExpect(status().isCreated());

        mockMvc.perform(get(getBaseUrl() + "/whoisit")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("testName"))
                .andExpect(jsonPath("email").value("testEmail"))
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

        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post(getBaseUrl() + "/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new UserModel(
                        "testName",
                        "testEmail",
                        "pass"
                )))).andExpect(status().isCreated());

        mockMvc.perform(get(getBaseUrl() + "/exit")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID")))
                .andExpect(status().isOk());
    }



    protected String getBaseUrl() {
        return "/user";
    }

    private final ObjectMapper mapper = new ObjectMapper();

    protected String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }
}
