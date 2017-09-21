package application.controllers;

import application.services.UserService;
import application.views.Response;
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
@CrossOrigin(origins = "*")
public class Controllers {

    private UserService users = new UserService();
    private HttpHeaders responseHeader = new HttpHeaders();

    public Controllers() {
        // Создание заголовков для CORS запросов
        this.responseHeader.set("Access-Control-Allow-Origin", "http://localhost:8081");
        this.responseHeader.set("Access-Control-Allow-Credentials", "true");
        this.responseHeader.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        this.responseHeader.set("Access-Control-Allow-Headers", "*");
    }


    @GetMapping(path = "/whoisit")
    public ResponseEntity<SuccessResponse> whoisit(HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<>(
                    responseHeader,
                    HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = users.findUserById(id);
        if (currentUser == null) {
            return new ResponseEntity<>(
                    responseHeader,
                    HttpStatus.GONE
            );
        }
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(currentUser),
                responseHeader,
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/exit")
    public ResponseEntity exit(HttpSession httpSession) {

        httpSession.invalidate();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/update", consumes = "application/json")
    public ResponseEntity<? extends Response> settings(
            @RequestBody UpdateUser parseBody,
            HttpSession httpSession) {

        // Проверки
        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Необходима авторизация"),
                    responseHeader,
                    HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = users.findUserById(id);
        if (currentUser == null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Профиль не найден"),
                    responseHeader,
                    HttpStatus.GONE
            );
        }
        if (!currentUser.getPassword().equals(parseBody.getOldPassword())) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Неверный пароль"),
                    responseHeader,
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
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(currentUser),
                responseHeader,
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/sign_up", consumes = "application/json")
    public ResponseEntity<? extends Response> signUp(
            @RequestBody User parseBody,
            HttpSession httpSession) {

        // Валидация
        if (parseBody.getPassword().length() < Response.MIN_PASSWORD_LENGTH) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Слишком короткий пароль"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST
            );
        }
        if (parseBody.getUsername().length() < Response.MIN_USERNAME_LENGTH) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Слишком короткий логин"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST
            );
        }

        if (users.findUserByUsername(parseBody.getUsername()) != null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Пользователь с таким логином уже существует"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST
            );
        }
        if (users.findUserByEmail(parseBody.getEmail()) != null) {
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Пользователь с такой почтой уже существует"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST
            );
        }

        httpSession.setAttribute("ID", users.addUser(parseBody));

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(parseBody),
                responseHeader,
                HttpStatus.CREATED
        );
    }

    @PostMapping(path = "/sign_in", consumes = "application/json")
    public ResponseEntity<? extends Response> signIn(
            @RequestBody SignInUser parseBody,
            HttpSession httpSession) {

        final User user = users.findUserByLogin(parseBody.getLogin());

        if (user == null) {
            httpSession.invalidate();
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Пользователя с таким логином не существует"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }
        if (!parseBody.getPassword().equals(user.getPassword())) {
            httpSession.invalidate();
            return new ResponseEntity<BadResponse>(
                    new BadResponse("Неверный логин или пароль"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }
        httpSession.setAttribute("ID", user.getId());
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(user),
                responseHeader,
                HttpStatus.OK
        );
    }

}
