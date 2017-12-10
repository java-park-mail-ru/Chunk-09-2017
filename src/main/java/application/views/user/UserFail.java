package application.views.user;

@SuppressWarnings("unused")
public final class UserFail {

    private String errorMessage;

    public UserFail(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}