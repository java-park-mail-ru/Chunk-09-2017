package application.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserUpdate extends UserBase {


    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty(value = "old_password", required = true)
    private String oldPassword;


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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserUpdate)) return false;

        final UserUpdate that = (UserUpdate) obj;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (!oldPassword.equals(that.oldPassword)) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + oldPassword.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
