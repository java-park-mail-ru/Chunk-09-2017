package application.services;

import application.dao.user.UserDaoJpa;
import org.springframework.stereotype.Service;

@Service
public class UserServiceJpa extends UserServiceAbstract {

    UserServiceJpa(UserDaoJpa userDao) {
        super(userDao);
    }
}
