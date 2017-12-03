package application.functional;

import application.models.user.UserSignUp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;


@Component
public class TestUtils {

    private final ObjectMapper mapper;
    private Long generatorID;

    public TestUtils(ObjectMapper mapper) {
        this.mapper = mapper;
        this.generatorID = 0L;
    }


    public String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    public UserSignUp getRandomUser() {
        ++generatorID;
        return new UserSignUp(
                "TestUsername_" + generatorID,
                "TestEmail_" + generatorID,
                "TestPassword_" + generatorID
        );
    }
}
