package application.integrated;

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

	final ObjectMapper mapper = new ObjectMapper();

	protected abstract String getBaseUrl();



	@Test
	public void testCreateSuccessUser() throws Exception {
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
	public void testCreateFailureUserNotEnouthFields() throws Exception {
		mockMvc.perform(post(getBaseUrl() + "/sign_up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(new UserModel())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreateFailureUserIncorrectFields() throws Exception {
		mockMvc.perform(post(getBaseUrl() + "/sign_up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(new UserModel("", "", ""))))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreateFailureUserWrongMediaType() throws Exception {
		mockMvc.perform(post(getBaseUrl() + "/sign_up")
				.contentType(MediaType.APPLICATION_PDF))
				.andExpect(status().is4xxClientError());
	}

	protected String toJson(Object o) throws JsonProcessingException {
		return mapper.writeValueAsString(o);
	}
}
