package main;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
public class Controllers {

    private HashSet<User> users = new HashSet<>();

    @PostMapping(value = "/sign_up", consumes = "application/json")
    public ResponseEntity<String> signUp(@RequestBody User requestBody) {

        // Создание заголовков для CORS запросов
        final HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("Access-Control-Allow-Origin", "http://localhost:8081");
        responseHeader.set("Access-Control-Allow-Credentials", "true");

        if (requestBody.getPassword().length() < 6)
            return new ResponseEntity<>("Слишком короткий пароль",
                    responseHeader, HttpStatus.NOT_ACCEPTABLE);
        if (requestBody.getUsername().length() < 4)
            return new ResponseEntity<>("Слишком короткий логин",
                    responseHeader, HttpStatus.NOT_ACCEPTABLE);

        for (User user: this.users) {
            if (requestBody.getUsername().equals(user.getUsername()))
                return new ResponseEntity<>("Пользователь с таким логином уже существует",
                                            responseHeader, HttpStatus.CONFLICT);
            if (requestBody.getEmail().equals(user.getEmail()))
                return new ResponseEntity<>("Пользователь с такой почтой уже существует",
                                            responseHeader, HttpStatus.CONFLICT);
        }

        this.users.add(requestBody);
        return new ResponseEntity<>(responseHeader, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/sign_up")
    public ResponseEntity preflight() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
