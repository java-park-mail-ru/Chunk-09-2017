package application.integrated;

import application.models.SignInModel;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public abstract class UserControllerAbstractTest {

	@Autowired
	private MockMvc mockMvc;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Test
	public void testSignUpUserSuccess() throws Exception {
		mockMvc.perform(post(getBaseUrl() + "/sign_up")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(new UserModel(
										"testName",
										"testEmail",
										"pass")
						)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("username").value("testName"))
				.andExpect(jsonPath("email").value("testEmail"))
				.andExpect(jsonPath("password").doesNotExist());
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




	protected abstract String getBaseUrl();

	private final ObjectMapper mapper = new ObjectMapper();

	protected String toJson(Object o) throws JsonProcessingException {
		return mapper.writeValueAsString(o);
	}
}
