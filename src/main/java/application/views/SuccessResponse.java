package application.views;

import application.models.User;

@SuppressWarnings("unused")
public final class SuccessResponse {

    private String username;
    private String email;

    public SuccessResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return email;
    }
}