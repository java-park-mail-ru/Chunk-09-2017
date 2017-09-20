package main;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

@RestController
public class Controllers {

    private HashSet<User> users = new HashSet<>();


    @GetMapping(value = "/whoisit")
    public ResponseEntity<User.Authorization> whoisit(HttpSession httpSession) {

        // Создание заголовков для CORS запросов
        final HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("Access-Control-Allow-Origin", "http://localhost:8081");
        responseHeader.set("Access-Control-Allow-Credentials", "true");

        final String username = (String) httpSession.getAttribute("username");
        if (username == null) {
            return new ResponseEntity<>(
                    responseHeader,
                    HttpStatus.UNAUTHORIZED
            );
        }
        return new ResponseEntity<>(
                new User.Authorization(username),
                responseHeader,
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/exit")
    public ResponseEntity exit(HttpSession httpSession) {

        httpSession.setAttribute("username", null);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/sign_up", consumes = "application/json")
    public ResponseEntity<? extends User.Response> signUp(
            @RequestBody User parseBody,
            HttpSession httpSession) {

        // Создание заголовков для CORS запросов
        final HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("Access-Control-Allow-Origin", "http://localhost:8081");
        responseHeader.set("Access-Control-Allow-Credentials", "true");

        // Валидация
        if (parseBody.getPassword().length() < 6)
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Слишком короткий пароль"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST);
        if (parseBody.getUsername().length() < 4)
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Слишком короткий логин"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST);

        for (User user: this.users) {
            if (parseBody.getUsername().equals(user.getUsername()))
                return new ResponseEntity<User.BadRequest>(
                        new User.BadRequest("Пользователь с таким логином уже существует"),
                        responseHeader, HttpStatus.BAD_REQUEST);
            if (parseBody.getEmail().equals(user.getEmail()))
                return new ResponseEntity<User.BadRequest>(
                        new User.BadRequest("Пользователь с такой почтой уже существует"),
                        responseHeader, HttpStatus.BAD_REQUEST);
        }

        this.users.add(parseBody);
        httpSession.setAttribute("username", parseBody.getUsername());

        return new ResponseEntity<User.Authorization>(
                new User.Authorization(parseBody),
                responseHeader,
                HttpStatus.CREATED);
    }

    @PostMapping(value = "/sign_in", consumes = "application/json")
    public ResponseEntity<? extends User.Response> signIn(
            @RequestBody User parseBody,
            HttpSession httpSession) {

        // TODO Andrew
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/change")
    public ResponseEntity<User> change(HttpSession httpSession) {

        final String username = (String) httpSession.getAttribute("username");
        if (username == null) {
            return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
        }

        final User currentUser = User.findUser(users, username);
        if (currentUser == null) {
            return new ResponseEntity<User>(HttpStatus.GONE);
        }

        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    @PostMapping(value = "/change", consumes = "application/json")
    public ResponseEntity<? extends User.Response> change(
            @RequestBody User parseBody,
            HttpSession httpSession) {

        // Создание заголовков для CORS запросов
        final HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("Access-Control-Allow-Origin", "http://localhost:8081");
        responseHeader.set("Access-Control-Allow-Credentials", "true");

        // Проверки
        final String oldUsername = (String) httpSession.getAttribute("username");
        if (oldUsername == null) {
            return new ResponseEntity<User.Response>(
                    responseHeader,
                    HttpStatus.UNAUTHORIZED
            );
        }
        final User currentUser = User.findUser(users, oldUsername);
        if (currentUser == null) {
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Профиль не найден"),
                    responseHeader,
                    HttpStatus.GONE
            );
        }
        if (!currentUser.getPassword().equals(parseBody.getOldPassword())) {
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Неверный пароль"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }

        currentUser.updateProfile(parseBody);
        httpSession.setAttribute("username", currentUser.getUsername());
        return new ResponseEntity<User.Response>(responseHeader, HttpStatus.OK);
    }


    @RequestMapping(path = "/sign_up")
    public ResponseEntity preflight() {
        // Без этого preflight-OPTIONS запросы не обрабатываются
        return new ResponseEntity(HttpStatus.OK);
    }
}
