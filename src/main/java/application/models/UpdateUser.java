package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class UpdateUser extends User {

    @JsonProperty("old_password")
    private String oldPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
