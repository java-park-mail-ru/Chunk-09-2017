package application.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;


public final class UserUpdate extends UserBase {

    private String username;
    private String email;
    @JsonProperty(required = true)
    private String oldPassword;

    public UserUpdate() { }

    public UserUpdate(String username, String email, String newPassword, String oldPassword) {
        this.username = username;
        this.email = email;
        this.oldPassword = oldPassword;
        this.setPassword(newPassword);
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserUpdate)) {
            return false;
        }

        final UserUpdate that = (UserUpdate) obj;

        if (username != null ? !username.equals(that.username) : that.username != null) {
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null) {
            return false;
        }
        if (!oldPassword.equals(that.oldPassword)) {
            return false;
        }
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null) {
            return false;
        }
        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = HASH_NUMBER * result + (email != null ? email.hashCode() : 0);
        result = HASH_NUMBER * result + oldPassword.hashCode();
        result = HASH_NUMBER * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = HASH_NUMBER * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
