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
    private HttpHeaders responseHeader = new HttpHeaders();

    public Controllers() {
        // Создание заголовков для CORS запросов
        this.responseHeader.set("Access-Control-Allow-Origin", "http://localhost:8081");
        this.responseHeader.set("Access-Control-Allow-Credentials", "true");
    }



    @GetMapping(path = "/whoisit")
    public ResponseEntity<User.Profile> whoisit(HttpSession httpSession) {

        final String username = (String) httpSession.getAttribute("username");
        if (username == null) {
            return new ResponseEntity<>(
                    responseHeader,
                    HttpStatus.UNAUTHORIZED
            );
        }
        return new ResponseEntity<>(
                new User.Profile(username, null),
                responseHeader,
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/exit")
    public ResponseEntity exit(HttpSession httpSession) {

        httpSession.invalidate();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path = "/settings")
    public ResponseEntity<User.Profile> change(HttpSession httpSession) {

        final String username = (String) httpSession.getAttribute("username");
        if (username == null) {
            return new ResponseEntity<>(responseHeader, HttpStatus.UNAUTHORIZED);
        }

        final User currentUser = User.findUser(users, username);

        if (currentUser == null) {
            return new ResponseEntity<>(responseHeader, HttpStatus.GONE);
        }

        return new ResponseEntity<User.Profile>(
                new User.Profile(currentUser),
                responseHeader,
                HttpStatus.OK
        );
    }



    @PostMapping(path = "/sign_up", consumes = "application/json")
    public ResponseEntity<? extends User.Response> signUp(
            @RequestBody User parseBody,
            HttpSession httpSession) {

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

        return new ResponseEntity<User.Profile>(
                new User.Profile(parseBody),
                responseHeader,
                HttpStatus.CREATED);
    }

    @PostMapping(path = "/sign_in", consumes = "application/json")
    public ResponseEntity<? extends User.Response> signIn(
            @RequestBody User parseBody,
            HttpSession httpSession) {

        final User user = User.findUser(users, parseBody.getUsername());
        if (user == null) {
            httpSession.setAttribute("username", null);
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Пользователя с таким логином не существует"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }
        if (!parseBody.getPassword().equals(user.getPassword())) {
            httpSession.setAttribute("username", null);
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Неверный логин или пароль"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }
        httpSession.setAttribute("username", user.getUsername());
        return new ResponseEntity<User.Profile>(
                new User.Profile(user),
                responseHeader,
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/settings", consumes = "application/json")
    public ResponseEntity<? extends User.Response> change(
            @RequestBody User parseBody,
            HttpSession httpSession) {

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
        users.remove(currentUser);
        if (User.findUser(users, parseBody.getUsername()) != null) {
            users.add(currentUser);
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Пользователь с таким именем уже существует!"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }
        if (User.findUser(users, parseBody.getEmail()) != null) {
            users.add(currentUser);
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Пользователь с такой почтой уже существует!"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }

        currentUser.updateProfile(parseBody);
        users.add(currentUser);
        httpSession.setAttribute("username", currentUser.getUsername());
        return new ResponseEntity<User.Response>(responseHeader, HttpStatus.OK);
    }



    @RequestMapping(path = "/sign_up")
    public ResponseEntity preflight1() {
        // Без этого preflight-OPTIONS запросы не обрабатываются
        return new ResponseEntity(HttpStatus.OK);
    }
    @RequestMapping(path = "/sign_in")
    public ResponseEntity preflight2() {
        // Без этого preflight-OPTIONS запросы не обрабатываются
        return new ResponseEntity(HttpStatus.OK);
    }
    @RequestMapping(path = "/settings")
    public ResponseEntity preflight3() {
        // Без этого preflight-OPTIONS запросы не обрабатываются
        return new ResponseEntity(HttpStatus.OK);
    }
}
