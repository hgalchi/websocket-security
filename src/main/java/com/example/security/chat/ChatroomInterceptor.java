package com.example.security.chat;

import com.example.security.chat.service.SocketService;
import com.example.security.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 메세지가 채널로 전송되기 전 사전처리
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ChatroomInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final SocketService socketService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        /*
         *CONNECT할 경우 유저의 accessToken을 사용해 인증 여부를 확인 후
         * STOMP HEADER에 식별가능한 값 email을 추가
         */
        if (accessor.getCommand().equals(StompCommand.CONNECT)) {
            String authorizationHeader = String.valueOf(accessor.getNativeHeader("Authorization").get(0));
            //token 인증
            String email =validateToken(authorizationHeader);
            //헤더에 user email 추가
            Map<String,Object> sessionAttributes = accessor.getSessionAttributes();
            sessionAttributes.put("email",email);
            accessor.setSessionAttributes(sessionAttributes);
        /*
         * SUBCRIBE할 경우 구독 경로를 검사한 후
         * 채팅방에 유저 추가
         */
        }else if(accessor.getCommand().equals(StompCommand.SUBSCRIBE)){
            //destination 경로 검사 "/app/chat/room/**","/topic/room/**"이외의 경로 구독은 거절
            String destination = accessor.getDestination();
            if (destination == null ||!destination.startsWith("/topic/room/")) {
                throw new MessageDeliveryException("Invalid destination");
            }
            //방인원 검사
            String email=Optional.of((String) accessor.getSessionAttributes().get("email"))
                    .orElseThrow(()->new MessageDeliveryException("Invalid email"));
            Long roomId = Long.parseLong(destination.split("/")[3]) ;
            socketService.join(email,roomId);
        /*
         * 클라이언트의 MESSAGE는 모두 거절
         */
        }else if(accessor.getCommand().equals(StompCommand.MESSAGE)){
            throw new MessageDeliveryException("Invalid command");
        }

        printStompFrame(message,accessor);

        return message;
    }
    /**
     * 토큰 인증
     * 만료나 변조 시, 예외를 터트린다.
     */
    private String validateToken(String authorizationHeader) {
        String token = resolveToken(authorizationHeader).trim();
        if (token == null) {
            throw new MessageDeliveryException("accessToken is null");
        }
        Claims claims = jwtUtil.validateToken(token);
        if(!jwtUtil.validateClaims(claims)) throw new MessageDeliveryException("token expried");

        return jwtUtil.getEmail(claims);
    }

    private String resolveToken(String authorizationHeader) {
        if(authorizationHeader == null || authorizationHeader.equals("null")) {
            throw new MessageDeliveryException("Authorization header is missing");
        }
        return authorizationHeader.substring("Bearer".length());
    }

    /**
     * 요청 Frame출력
     */
    private void printStompFrame(Message<?> message,StompHeaderAccessor accessor) {
        System.out.println("Command: " + accessor.getCommand());
        MessageHeaders headers = message.getHeaders();
        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
        if (multiValueMap != null) {
            for (Map.Entry<String, List<String>> head : multiValueMap.entrySet()) {
                System.out.println(head.getKey() + "#" + head.getValue());
            }
        }
    }
}
