package application;

public class Views {

    static final int MIN_USERNAME_LENGTH = 4;
    static final int MIN_PASSWORD_LENGTH = 6;

    public static class Response { }

    public static final class Profile extends Response {

        private String username;
        private String email;

        Profile(String username, String email) {
            this.username = username;
            this.email = email;
        }

        Profile(User user) {
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

    public static final class BadRequest extends Response {
        private String errorMessage;

        BadRequest(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
