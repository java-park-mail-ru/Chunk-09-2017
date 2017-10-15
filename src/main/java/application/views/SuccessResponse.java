package application.views;

import application.models.UserModel;

@SuppressWarnings("unused")
public final class SuccessResponse {

    private String username;
    private String email;

    public SuccessResponse(UserModel user) {
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