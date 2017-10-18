package application.models;

import application.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;


@Service
@SuppressWarnings("unused")
public class UserModel {

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty(value = "password")
    private String password;

    private Long id;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof UserModel)) {
            return false;
        }

        final UserModel userModel = (UserModel) object;
        return (
                username.equals(userModel.username)
                        && password.equals(userModel.password)
                        && email.equals(userModel.email)
        );
    }

    static final int HASH_NUMBER = 31;

    @Override
    public int hashCode() {

        int result = 0;
        if (username != null) {
            result += username.hashCode();
        }
        result *= HASH_NUMBER;
        if (email != null) {
            result += email.hashCode();
        }
        result *= HASH_NUMBER;
        if (password != null) {
            result += password.hashCode();
        }
        result *= HASH_NUMBER;
        if (id != null) {
            result += id.hashCode();
        }
        return result;
    }

    public UserModel() { }

    public UserModel(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public UserModel(UserEntity userEntity) {
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
        this.password = userEntity.getPassword();
        this.id = userEntity.getId();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
