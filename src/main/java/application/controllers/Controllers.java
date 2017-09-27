package application.controllers;

import application.services.UserService;
import application.models.SignInUser;
import application.models.UpdateUser;
import application.models.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import application.views.*;

@SuppressWarnings("all")
@RestController
@CrossOrigin(origins = "https://tower-defense.herokuapp.com")
public class Controllers {

    private UserService users = new UserService();
    private HttpHeaders responseHeader = new HttpHeaders();


    @GetMapping(path = "/whoisit")
    public ResponseEntity<GoodResponse> whoisit(HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<>(
                    HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = users.findUserById(id);
        if (currentUser == null) {
            return new ResponseEntity<>(
                    HttpStatus.FORBIDDEN
            );
        }
        return new ResponseEntity<GoodResponse>(
                new GoodResponse(currentUser),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/exit")
    public ResponseEntity exit(HttpSession httpSession) {

        httpSession.invalidate();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/update", consumes = "application/json")
    public ResponseEntity<?> settings(
            @RequestBody UpdateUser parseBody,
            HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Необходима авторизация"),
                    HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = users.findUserById(id);
        if (currentUser == null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Профиль не найден"),
                    HttpStatus.GONE
            );
        }
        if (!currentUser.getPassword().equals(parseBody.getOldPassword())) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Неверный пароль"),
                    HttpStatus.FORBIDDEN
            );
        }
        if (!currentUser.getUsername().equals(parseBody.getUsername())) {
            if (users.findUserByUsername(parseBody.getUsername()) != null) {
                return new ResponseEntity<BadResponse>(
                        new BadResponse("Пользователь с таким именем уже существует!"),
                        responseHeader,
                        HttpStatus.FORBIDDEN
                );
            }
        }
        if (!currentUser.getEmail().equals(parseBody.getEmail())) {
            if (users.findUserByEmail(parseBody.getEmail()) != null) {
                return new ResponseEntity<BadResponse>(
                        new BadResponse("Пользователь с такой почтой уже существует!"),
                        responseHeader,
                        HttpStatus.FORBIDDEN
                );
            }
        }
        currentUser.updateProfile(parseBody);
        return new ResponseEntity<GoodResponse>(
                new GoodResponse(currentUser),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/sign_up", consumes = "application/json")
    public ResponseEntity<?> signUp(
            @RequestBody User parseBody,
            HttpSession httpSession) {


        final String errorMessage = UserService.userValidation(parseBody);
        if (errorMessage != null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse(errorMessage),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (users.findUserByUsername(parseBody.getUsername()) != null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Пользователь с таким логином уже существует"),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (users.findUserByEmail(parseBody.getEmail()) != null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Пользователь с такой почтой уже существует"),
                    HttpStatus.BAD_REQUEST
            );
        }

        httpSession.setAttribute("ID", users.addUser(parseBody));

        return new ResponseEntity<GoodResponse>(
                new GoodResponse(parseBody),
                HttpStatus.CREATED
        );
    }

    @PostMapping(path = "/sign_in", consumes = "application/json")
    public ResponseEntity<?> signIn(
            @RequestBody SignInUser parseBody,
            HttpSession httpSession) {

        final User user = users.findUserByLogin(parseBody.getLogin());

        if (user == null) {
            httpSession.invalidate();
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Пользователя с таким логином не существует"),
                    HttpStatus.FORBIDDEN
            );
        }
        if (!parseBody.getPassword().equals(user.getPassword())) {
            httpSession.invalidate();
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Неверный логин или пароль"),
                    HttpStatus.FORBIDDEN
            );
        }
        httpSession.setAttribute("ID", user.getId());
        return new ResponseEntity<GoodResponse>(
                new GoodResponse(user),
                HttpStatus.OK
        );
    }

}
