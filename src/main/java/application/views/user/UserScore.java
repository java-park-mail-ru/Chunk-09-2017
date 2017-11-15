package application.views.user;

import application.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserScore {

    @JsonProperty
    private String username;

    @JsonProperty
    private String email;

    @JsonProperty
    private Integer score;

    public UserScore() { }

    public UserScore(UserEntity userEntity) {
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
        this.score = 0;
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
