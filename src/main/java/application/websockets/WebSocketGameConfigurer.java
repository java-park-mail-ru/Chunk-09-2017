package application.websockets;

import application.controllers.game.GameSocketHandlerLobby;
import application.controllers.game.GameSocketHandlerPlay;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


@Component
@EnableWebSocket
public class WebSocketGameConfigurer implements WebSocketConfigurer {

    private final GameSocketHandlerLobby gameSocketHandlerLobby;
    private final GameSocketHandlerPlay gameSocketHandlerPlay;

    WebSocketGameConfigurer(GameSocketHandlerLobby gameSocketHandlerLobby,
                            GameSocketHandlerPlay gameSocketHandlerPlay) {

        this.gameSocketHandlerLobby = gameSocketHandlerLobby;
        this.gameSocketHandlerPlay = gameSocketHandlerPlay;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketGameHandler(
                gameSocketHandlerLobby, gameSocketHandlerPlay), "/play")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
