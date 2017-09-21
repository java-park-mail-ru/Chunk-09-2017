package application;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "*")
public class Controllers {

//    private HashSet<User> users = new HashSet<>();
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
    public ResponseEntity<Views.SuccessResponse> whoisit(HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<>(
                    responseHeader,
                    HttpStatus.UNAUTHORIZED
            );
        }
        return new ResponseEntity<>(
                new Views.SuccessResponse(
                        users.findUserById(id).getUsername(),
                        users.findUserById(id).getEmail()
                ),
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
    public ResponseEntity<Views.SuccessResponse> settings(HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<>(responseHeader, HttpStatus.UNAUTHORIZED);
        }

        final User currentUser = users.findUserById(id);

        if (currentUser == null) {
            return new ResponseEntity<>(responseHeader, HttpStatus.GONE);
        }

        return new ResponseEntity<Views.SuccessResponse>(
                new Views.SuccessResponse(currentUser),
                responseHeader,
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/settings", consumes = "application/json")
    public ResponseEntity<? extends Views.Response> settings(
            @RequestBody User parseBody,
            HttpSession httpSession) {

        // Проверки
        final Long id = (Long) httpSession.getAttribute("ID");
        if (id == null) {
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Необходима авторизация"),
                    responseHeader,
                    HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = users.findUserById(id);
        if (currentUser == null) {
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Профиль не найден"),
                    responseHeader,
                    HttpStatus.GONE
            );
        }
        if (!currentUser.getPassword().equals(parseBody.getOldPassword())) {
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Неверный пароль"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }
        if (!currentUser.getUsername().equals(parseBody.getUsername())) {
            if (users.findUserByUsername(parseBody.getUsername()) != null) {
                return new ResponseEntity<Views.BadResponse>(
                        new Views.BadResponse("Пользователь с таким именем уже существует!"),
                        responseHeader,
                        HttpStatus.FORBIDDEN
                );
            }
        }
        if (!currentUser.getEmail().equals(parseBody.getEmail())) {
            if (users.findUserByEmail(parseBody.getEmail()) != null) {
                return new ResponseEntity<Views.BadResponse>(
                        new Views.BadResponse("Пользователь с такой почтой уже существует!"),
                        responseHeader,
                        HttpStatus.FORBIDDEN
                );
            }
        }
        currentUser.updateProfile(parseBody);
        return new ResponseEntity<Views.Response>(responseHeader, HttpStatus.OK);
    }

    @PostMapping(path = "/sign_up", consumes = "application/json")
    public ResponseEntity<? extends Views.Response> signUp(
            @RequestBody User parseBody,
            HttpSession httpSession) {

        // Валидация
        if (parseBody.getPassword().length() < Views.MIN_PASSWORD_LENGTH) {
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Слишком короткий пароль"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST
            );
        }
        if (parseBody.getUsername().length() < Views.MIN_USERNAME_LENGTH) {
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Слишком короткий логин"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST
            );
        }

        if (users.findUserByUsername(parseBody.getUsername()) != null) {
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Пользователь с таким логином уже существует"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST
            );
        }
        if (users.findUserByEmail(parseBody.getEmail()) != null) {
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Пользователь с такой почтой уже существует"),
                    responseHeader,
                    HttpStatus.BAD_REQUEST
            );
        }

        httpSession.setAttribute("ID", users.addUser(parseBody));

        return new ResponseEntity<Views.SuccessResponse>(
                new Views.SuccessResponse(parseBody),
                responseHeader,
                HttpStatus.CREATED
        );
    }

    @PostMapping(path = "/sign_in", consumes = "application/json")
    public ResponseEntity<? extends Views.Response> signIn(
            @RequestBody User parseBody,
            HttpSession httpSession) {

        final User user = users.findUserByLogin(parseBody.getUsername());

        if (user == null) {
            httpSession.invalidate();
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Пользователя с таким логином не существует"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }
        if (!parseBody.getPassword().equals(user.getPassword())) {
            httpSession.invalidate();
            return new ResponseEntity<Views.BadResponse>(
                    new Views.BadResponse("Неверный логин или пароль"),
                    responseHeader,
                    HttpStatus.FORBIDDEN
            );
        }
        httpSession.setAttribute("ID", user.getId());
        return new ResponseEntity<Views.SuccessResponse>(
                new Views.SuccessResponse(user),
                responseHeader,
                HttpStatus.OK
        );
    }

}
