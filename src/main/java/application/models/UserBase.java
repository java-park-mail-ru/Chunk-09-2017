package application.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class UserBase {

    @JsonProperty("password")
    private String password;

    private Long id;

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

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

    public static final int HASH_NUMBER = 31;
}
