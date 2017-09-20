package main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Username:\t" + this.username + '\n' +
                "Email:\t" + this.email + '\n' +
                "Password:\t" + this.password + '\n';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) return false;
        final User second = (User) obj;
        return (this.username.equals(second.username) &&
                this.password.equals(second.password) &&
                this.email.equals(second.email));
    }
}
