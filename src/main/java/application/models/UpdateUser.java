package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;


@SuppressWarnings("unused")
public class UpdateUser extends UserModel {

    @JsonProperty("old_password")
    private String oldPassword;

    public UpdateUser() { }

    public UpdateUser(String username, String email, String password, String oldPassword) {
        super(username, email, password);
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }
}
