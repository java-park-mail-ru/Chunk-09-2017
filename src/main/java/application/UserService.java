package application;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;


public class UserService {

    private HashMap<Long, User> users = new HashMap<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    public Long addUser(User user) {
        final long id = ID_GENERATOR.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        return id;
    }

    public User findUserById(Long id) {
        return users.get(id);
    }

    public User findUserByUsername(String username) {

        for (User user : users.values()) {
            if (username.equals(user.getUsername()))
                return user;
        }
        return null;
    }

    public User findUserByEmail(String email) {

        for (User user : users.values()) {
            if (email.equals(user.getEmail()))
                return user;
        }
        return null;
    }

    public User findUserByLogin(String login) {

        for (User user : users.values()) {
            if (login.equals(user.getUsername()) || login.equals(user.getEmail()))
                return user;
        }
        return null;
    }

    public void updateProfile(Long id, User newProfile) {
        users.get(id).updateProfile(newProfile);
    }

}
