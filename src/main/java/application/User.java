package application;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;


@SuppressWarnings("unused")
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

        if (!newProfile.username.isEmpty()) {
            this.username = newProfile.username;
        }

        if (!newProfile.email.isEmpty()) {
            this.email = newProfile.email;
        }

        if (!newProfile.password.isEmpty()) {
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
        return "Username:\t" + this.username + '\n'
                + "Email:\t" + this.email + '\n'
                + "Password:\t" + this.password + '\n';
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof User)) return false;
//        final User second = (User) obj;
//        return (this.username.equals(second.username) &&
//                this.password.equals(second.password) &&
//                this.email.equals(second.email));
//    }


    // Getters & setters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    String getOldPassword() {
        return oldPassword;
    }

    String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    void setPassword(String password) {
        this.password = password;
    }
}
