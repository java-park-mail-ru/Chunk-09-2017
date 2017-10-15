package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInUser extends UserModel {

    @JsonProperty("login")
    private String login;

    public String getLogin() {
        return login;
    }
}
