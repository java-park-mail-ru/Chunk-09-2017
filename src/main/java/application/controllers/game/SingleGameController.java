package application.controllers.game;


import application.models.game.preGame;
import application.models.game.Snapshot;
import application.services.game.GameService;
import application.services.user.UserService;
import application.views.user.UserFail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(path = "/game")
@CrossOrigin(origins = "*")
public class SingleGameController {

    private final GameService gameService;
    private final UserService userService;

    SingleGameController(GameService gameService,
                         UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

//    @PostMapping(path = "/multi/create")
//    public ResponseEntity createMultiGame(
//            @RequestBody preGame preGame,
//            HttpSession httpSession) {
//        final Long userID = (Long) httpSession.getAttribute("ID");
//        if (userID == null) {
//            return new ResponseEntity<>(
//                    new UserFail("Need to sign up"),
//                    HttpStatus.UNAUTHORIZED
//            );
//        }
//        final Long gameID = gameService.createGame(preGame);
//        if (gameID == null) {
//            return new ResponseEntity<>(
//                    new UserFail("Bad request"),
//                    HttpStatus.BAD_REQUEST
//            );
//        }
//        gameService.addPlayer(gameID, userService.getUserById(userID));
//        return new ResponseEntity<>(
//                "{\"gameID\": \"" + gameID + "\"}",
//                HttpStatus.CREATED
//        );
//    }

    @PostMapping(path = "/single/create")
    public ResponseEntity createSingleGame(
            @RequestBody preGame preGame,
            HttpSession httpSession) {
        final Long userID = (Long) httpSession.getAttribute("ID");
        if (userID == null) {
            return new ResponseEntity<>(
                    new UserFail("Need to sign up"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        final Long gameID = gameService.createGame(preGame);
        gameService.addPlayer(gameID, userService.getUserById(userID));
        for (int i = 1; i < preGame.getMaxPlayers(); ++i) {
            gameService.addBot(gameID);
        }
        if (gameID == null) {
            return new ResponseEntity<>(
                    new UserFail("Bad request"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
                "{\"gameID\": \"" + gameID + "\"}",
                HttpStatus.CREATED
        );
    }

    @GetMapping(path = "/complete")
    public ResponseEntity checkPrepareGameStatus(
            @RequestParam(name = "gameID") Long gameID) {

        if (!gameService.getPrepareGameStatus(gameID)) {
            return new ResponseEntity<>(
                    gameService.getPrepareGamePlayers(gameID),
                    HttpStatus.PROCESSING
            );
        } else {
            return new ResponseEntity<>(
                    gameService.getReadyGame(gameID),
                    HttpStatus.CREATED
            );
        }
    }

    @PutMapping(path = "/play")
    public ResponseEntity play(
            @RequestBody Snapshot snapshot,
            HttpSession httpSession) {

        final Long userID = (Long) httpSession.getAttribute("ID");
        if (userID == null) {
            return new ResponseEntity<>(
                    new UserFail("Need to sign up"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        return new ResponseEntity<>(
                gameService.play(userID, snapshot),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/status")
    public ResponseEntity getUpdateField(
            @RequestBody Snapshot snapshot,
            HttpSession httpSession)  {

        final Long userID = (Long) httpSession.getAttribute("ID");
        if (userID == null) {
            return new ResponseEntity<>(
                    new UserFail("Need to sign up"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        return new ResponseEntity<>(
                gameService.waitingAnotherPlayer(userID, snapshot),
                HttpStatus.OK
        );
    }
}
