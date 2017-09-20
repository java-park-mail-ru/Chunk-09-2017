package main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;

class User {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("old_password")
    private String oldPassword;


    public void updateProfile(final User newProfile) {

        if (newProfile.username != null)
            this.username = newProfile.username;

        if (newProfile.email != null)
            this.username = newProfile.email;

        if (newProfile.password != null) {
            this.oldPassword = this.password;
            this.password = newProfile.password;
        }
    }

    public static User findUser(
            final HashSet<User> users,
            final String login) {
        for (User user: users) {
            if (login.equals(user.username) || login.equals(user.email)) {
                return user;
            }
        }
        return null;
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


    // Getters & setters
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getOldPassword() {
        return oldPassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    // Response-classes
    public static class Response {};

    public static final class Authorization extends Response {
        private String username;

        Authorization(String username) {
            this.username = username;
        }
        Authorization(User user) {
            this.username = user.getUsername();
        }

        public String getUsername() {
            return this.username;
        }
    }

    public static final class BadRequest extends Response {
        private String errorMessage;

        BadRequest(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
