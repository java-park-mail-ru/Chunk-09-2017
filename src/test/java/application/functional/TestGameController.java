package application.functional;

import application.models.user.UserModel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class TestGameController {

    @Autowired
    private MockMvc mockMvc;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testCreateMultiGameSuccess() throws Exception {
        final UserModel testUser = TestUtils.getRandomUser();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post("/user/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(testUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(baseUrl + "/multi/create")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(new TestUtils.TestPlayer())))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("gameID").exists())
                .andDo(print());
    }

    @Test
    public void testCreateSingleGameSuccess() throws Exception {
        final UserModel testUser = TestUtils.getRandomUser();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post("/user/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(testUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(baseUrl + "/single/create")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(new TestUtils.TestPlayer())))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("gameID").exists())
                .andDo(print());
    }

    @Test
    public void testCompleteSingleGameSuccess() throws Exception {
        final UserModel testUser = TestUtils.getRandomUser();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc.perform(post("/user/sign_up")
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(testUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(baseUrl + "/single/create")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJson(new TestUtils.TestPlayer())))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("gameID").exists())
                .andDo(print());

        mockMvc.perform(get(baseUrl + "/complete/")
                .param("gameID", "0")
                .sessionAttr("ID", mockHttpSession.getAttribute("ID")))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(print());
    }


    private final String baseUrl = "/game";
}
