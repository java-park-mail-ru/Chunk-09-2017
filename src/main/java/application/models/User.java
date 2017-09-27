package application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class User {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty(value = "password")
    private String password;

    private long id;


    public void updateProfile(final User newProfile) {

        if (!newProfile.username.isEmpty()) {
            this.username = newProfile.username;
        }

        if (!newProfile.email.isEmpty()) {
            this.email = newProfile.email;
        }

        if (!newProfile.password.isEmpty()) {
            this.password = newProfile.password;
        }
    }


    @Override
    public String toString() {
        return "Username:\t" + this.username + '\n'
                + "Email:\t" + this.email + '\n'
                + "Password:\t" + this.password + '\n';
    }


    @JsonIgnore
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setId(long id) {
        this.id = id;
    }
}
