package application.models;

import application.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserSignUp extends UserBase {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    public UserSignUp() { }

    public UserSignUp(UserEntity userEntity) {

        this.setPassword(userEntity.getPassword());
        this.setId(userEntity.getId());
        this.email = userEntity.getEmail();
        this.username = userEntity.getUsername();
    }

    public UserSignUp(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.setPassword(password);
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
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserSignUp)) {
            return false;
        }

        final UserSignUp rightObj = (UserSignUp) obj;

        if (username != null ? !username.equals(rightObj.username) : rightObj.username != null) {
            return false;
        }
        if (email != null ? !email.equals(rightObj.email) : rightObj.email != null) {
            return false;
        }
        if (getPassword() != null ? !getPassword() .equals(rightObj.getPassword()) : rightObj.getPassword()  != null) {
            return false;
        }
        return getId() != null ? getId().equals(rightObj.getId()) : rightObj.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = HASH_NUMBER * result + (email != null ? email.hashCode() : 0);
        result = HASH_NUMBER * result + (getPassword()  != null ? getPassword() .hashCode() : 0);
        result = HASH_NUMBER * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
