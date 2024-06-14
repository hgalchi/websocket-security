package com.example.security.chat;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class WebSocketEventListner {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListner.class);
    private final SimpMessagingTemplate template;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        logger.info("Received websocket connect event");
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        logger.info("Received a new web socket subcribe");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttribute = accessor.getSessionAttributes();
        String username = (String) sessionAttribute.get("session");

        template.convertAndSend("/topic/room/2",username+"입장했습니다");
    }


    @EventListener
    public void handleWebSocketDisconnectListner(SessionDisconnectEvent event) {
        logger.info("Received a new web socket disconnect");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

    }

}
