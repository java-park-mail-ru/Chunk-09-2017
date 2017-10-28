package application.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserSignUp extends UserBase {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    public UserSignUp() { }

    public UserSignUp(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

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


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserSignUp)) return false;

        final UserSignUp rightObj = (UserSignUp) obj;

        if (username != null ? !username.equals(rightObj.username) : rightObj.username != null) return false;
        if (email != null ? !email.equals(rightObj.email) : rightObj.email != null) return false;
        if (password != null ? !password.equals(rightObj.password) : rightObj.password != null) return false;
        return id != null ? id.equals(rightObj.id) : rightObj.id == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
