//package application.services;
//
//import application.models.UserModel;
//
//import java.util.HashMap;
//import java.util.concurrent.atomic.AtomicLong;
//
//
//public class UserService {
//
//    private static final int MIN_USERNAME_LENGTH = 4;
//    private static final int MIN_PASSWORD_LENGTH = 6;
//
//    private HashMap<Long, UserModel> users = new HashMap<>();
//    private static final AtomicLong ID_GENERATOR = new AtomicLong();
//
//    public Long addUser(UserModel user) {
//        final long id = ID_GENERATOR.getAndIncrement();
//        user.setId(id);
//        users.put(id, user);
//        return id;
//    }
//
//    public UserModel findUserById(Long id) {
//        return users.get(id);
//    }
//
//    public UserModel findUserByUsername(String username) {
//
//        for (UserModel user : users.values()) {
//            if (username.equals(user.getUsername())) {
//                return user;
//            }
//        }
//        return null;
//    }
//
//    public UserModel findUserByEmail(String email) {
//
//        for (UserModel user : users.values()) {
//            if (email.equals(user.getEmail())) {
//                return user;
//            }
//        }
//        return null;
//    }
//
//    public UserModel findUserByLogin(String login) {
//
//        for (UserModel user : users.values()) {
//            if (login.equals(user.getUsername()) || login.equals(user.getEmail())) {
//                return user;
//            }
//        }
//        return null;
//    }
//
//    public static String userValidation(UserModel user) {
//
//        if (user.getPassword().length() < MIN_PASSWORD_LENGTH) {
//            return "Слишком короткий пароль";
//        }
//        if (user.getUsername().length() < MIN_USERNAME_LENGTH) {
//            return "Слишком короткий логин";
//        }
//        return null;
//    }
//}
