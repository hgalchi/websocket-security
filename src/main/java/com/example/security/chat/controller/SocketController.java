package com.example.security.chat.controller;

import com.example.security.chat.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpOutputMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Log4j2
public class SocketController {

    private final SimpMessagingTemplate template;
    private final SocketService socketService;
    private final ServerProperties serverProperties;

    // todo : 초기데이터 전송
    @SubscribeMapping(value = "/chat/room/{roomId}/data")
    public void subscribe(StompHeaderAccessor accessor, @DestinationVariable Long roomId, @Header("simpSessionAttributes") Map<String, Object> attributes) {
        /*
        if (socketService.join(roomId, userSession)) return "초기 데이터 실시간 전송";
        return "마감되었습니다.";*/
    }

    //브로드캐스팅
    @MessageMapping(value = "/chat/room/{roomId}/message")
    public void message(@Payload String message, @DestinationVariable Long roomId, @Header("simpSessionAttributes") Map<String, Object> attributes) {

        String email = attributes.get("email").toString();
        socketService.saveMessage(message, roomId, email);
        template.convertAndSend("/topic/room/" + roomId, message);

    }
    //채팅방 퇴장
    @MessageMapping("/chat/room/{roomId}/leave")
    public void leave(@DestinationVariable Long roomId, @Header("simpSessionAttributes") Map<String, Object> attributes) {
        String userSession=attributes.get("userSession").toString();
        socketService.leave(userSession, roomId);
        template.convertAndSend("/topic/room/" + roomId, userSession + "님이 퇴장하셨습니다.");

    }

    //todo : 1:1 채팅
    @MessageMapping("/user")
    public void sendSpecific(
                               @Payload Message msg,
                               Principal user,
                               @Header("simpSessionId") String sessionId) {

        template.convertAndSendToUser(Objects.requireNonNull(msg.getHeaders().get("user")).toString(), "/user/queue/specific-user", "send to specific user");

    }

}
