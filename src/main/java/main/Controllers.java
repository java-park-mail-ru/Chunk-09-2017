package main;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.Console;
import java.util.HashSet;

@RestController
public class Controllers {

    private HashSet<User> users = new HashSet<>();


    @GetMapping(value = "/whoisit")
    public ResponseEntity<User.Authorization> setSession(HttpSession httpSession) {

        // Создание заголовков для CORS запросов
        final HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("Access-Control-Allow-Origin", "http://localhost:8081");
        responseHeader.set("Access-Control-Allow-Credentials", "true");

        final String username = (String) httpSession.getAttribute("username");
        if (username == null) {
            return new ResponseEntity<>(responseHeader, HttpStatus.UNAUTHORIZED);
        }
        System.out.println(username);
        return new ResponseEntity<>(
                new User.Authorization(username),
                responseHeader,
                HttpStatus.OK);
    }


    @PostMapping(value = "/sign_up", consumes = "application/json")
    public ResponseEntity<? extends User.Response> signUp(
            @RequestBody User requestBody,
            HttpSession httpSession) {

        // Создание заголовков для CORS запросов
        final HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("Access-Control-Allow-Origin", "http://localhost:8081");
        responseHeader.set("Access-Control-Allow-Credentials", "true");

        // Валидация
        if (requestBody.getPassword().length() < 6)
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Слишком короткий пароль"),
                    responseHeader,
                    HttpStatus.NOT_ACCEPTABLE);
        if (requestBody.getUsername().length() < 4)
            return new ResponseEntity<User.BadRequest>(
                    new User.BadRequest("Слишком короткий логин"),
                    responseHeader,
                    HttpStatus.NOT_ACCEPTABLE);

        for (User user: this.users) {
            if (requestBody.getUsername().equals(user.getUsername()))
                return new ResponseEntity<User.BadRequest>(
                        new User.BadRequest("Пользователь с таким логином уже существует"),
                        responseHeader, HttpStatus.CONFLICT);
            if (requestBody.getEmail().equals(user.getEmail()))
                return new ResponseEntity<User.BadRequest>(
                        new User.BadRequest("Пользователь с такой почтой уже существует"),
                        responseHeader, HttpStatus.CONFLICT);
        }

        this.users.add(requestBody);
        httpSession.setAttribute("username", requestBody.getUsername());

        return new ResponseEntity<User.Authorization>(
                new User.Authorization(requestBody),
                responseHeader,
                HttpStatus.CREATED);
    }

    @PostMapping(value = "/sign_in", consumes = "application/json")
    public ResponseEntity<? extends User.Response> signIn(
            @RequestBody User requestBody,
            HttpSession httpSession) {

        // TODO Andrew
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "/sign_up")
    public ResponseEntity preflight() {
        // Без этого preflight-OPTIONS запросы не обрабатываются
        return new ResponseEntity(HttpStatus.OK);
    }
}
