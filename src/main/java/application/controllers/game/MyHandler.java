package application.controllers.game;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



public class MyHandler extends TextWebSocketHandler {

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println(message.getPayload());
		ObjectMapper mapper = new ObjectMapper();
		session.sendMessage(new TextMessage(mapper.writeValueAsString("Hello, client!")));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession var1) throws Exception {
		System.out.println("Соединение установлено");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession var1, CloseStatus var2) throws Exception {
		System.out.println("Соединение разорвано!");
	}
}
