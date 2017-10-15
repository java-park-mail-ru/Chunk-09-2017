package application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateUser extends UserModel {

    @JsonProperty("old_password")
    private String oldPassword;

    @JsonIgnore
    public String getOldPassword() {
        return oldPassword;
    }
}
