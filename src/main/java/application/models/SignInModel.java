package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;


@SuppressWarnings("unused")
public class SignInModel extends UserModel {

    @JsonProperty(value = "login", required = true)
    private String login;

    public SignInModel() { }

    public SignInModel(String login, String password) {
        super();
        this.setPassword(password);
        this.login = login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
