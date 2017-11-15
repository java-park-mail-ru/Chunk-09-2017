package application.websockets;

import application.controllers.game.GameSocketController1xx;
import application.controllers.game.GameSocketController2xx;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


@Component
@EnableWebSocket
public class WebSocketGameConfigurer implements WebSocketConfigurer {

    private final GameSocketController1xx gameSocketController1xx;
    private final GameSocketController2xx gameSocketController2xx;

    WebSocketGameConfigurer(GameSocketController1xx controller1xx,
                            GameSocketController2xx controller2xx) {

        this.gameSocketController1xx = controller1xx;
        this.gameSocketController2xx = controller2xx;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketGameHandler(
                gameSocketController1xx, gameSocketController2xx), "/play")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
