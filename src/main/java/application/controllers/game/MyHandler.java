package application.controllers.game;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



@Component
public class MyHandler extends TextWebSocketHandler {


	@Override
	public void afterConnectionEstablished(WebSocketSession var1) throws Exception {

	}

	@Override
	public void handleMessage(WebSocketSession var1, WebSocketMessage<?> var2) throws Exception {

	}

	@Override
	public void handleTransportError(WebSocketSession var1, Throwable var2) throws Exception {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession var1, CloseStatus var2) throws Exception {

	}

	@Override
	public boolean supportsPartialMessages() {
		return true;
	}
}
