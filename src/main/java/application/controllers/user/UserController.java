package application.controllers.user;

import application.exceptions.user.UserException;
import application.models.user.UserSignIn;
import application.models.user.UserSignUp;
import application.models.user.UserUpdate;
import application.services.user.UserService;
import application.services.user.UserServiceJpa;
import application.views.user.UserFail;
import application.views.user.UserSuccess;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping(path = "/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService service;

    UserController(UserServiceJpa service) {
        this.service = service;
    }

    @GetMapping(path = "/whoisit")
    public ResponseEntity whoisit(HttpSession httpSession) {
        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<>(
                    new UserFail("Need to sign up"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        return new ResponseEntity<>(
                new UserSuccess(service.getUserById(id)),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/exit")
    public ResponseEntity exit(HttpSession httpSession) {
        httpSession.invalidate();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/update", consumes = "application/json")
    public ResponseEntity settings(
            @RequestBody UserUpdate userUpdate,
            HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<>(
                    new UserFail("Need to sign up"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        final UserSignUp userUpdated = service.updateUserProfile(userUpdate, id);
        httpSession.setAttribute("ID", userUpdated.getId());
        return new ResponseEntity<>(
                new UserSuccess(userUpdated),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/sign_up", consumes = "application/json")
    public ResponseEntity signUp(
            @RequestBody UserSignUp user,
            HttpSession httpSession) {

        httpSession.setAttribute("ID", service.addUser(user));
        return new ResponseEntity<>(
                new UserSuccess(user),
                HttpStatus.CREATED
        );
    }

    @PostMapping(path = "/sign_in", consumes = "application/json")
    public ResponseEntity signIn(
            @RequestBody UserSignIn parseBody,
            HttpSession httpSession) {

        final UserSignUp user = service.signInByLogin(
                parseBody.getLogin(),
                parseBody.getPassword()
        );
        httpSession.setAttribute("ID", user.getId());
        return new ResponseEntity<>(
                new UserSuccess(user),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/userlist")
    public ResponseEntity getUserList() {
        return new ResponseEntity<>(
                service.getUserList(),
                HttpStatus.OK
        );
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<UserFail> handleUserServiceError(UserException exception) {
        exception.printStackTrace();
        return new ResponseEntity<>(
                new UserFail(exception.getErrorMessage()),
                exception.getErrorCode()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UserFail> handleUnexpectedException(RuntimeException exception) {
        exception.printStackTrace();
        return new ResponseEntity<>(
                new UserFail("Unexpected error"),
                HttpStatus.I_AM_A_TEAPOT
        );
    }
}