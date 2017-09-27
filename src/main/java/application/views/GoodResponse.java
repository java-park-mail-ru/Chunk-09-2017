package application.views;

import application.models.User;

@SuppressWarnings("unused")
public final class GoodResponse {

    private String username;
    private String email;

    public GoodResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}