package application.views;


@SuppressWarnings("unused")
public final class FailResponse {

    private String errorMessage;

    public FailResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}