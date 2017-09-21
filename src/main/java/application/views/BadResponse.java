package application.views;


@SuppressWarnings("unused")
public final class BadResponse extends Response {

    private String errorMessage;

    public BadResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}