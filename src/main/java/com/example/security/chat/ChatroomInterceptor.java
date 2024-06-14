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
        if (accessor.getCommand().equals(StompCommand.CONNECT)) {
            //인증된 사용자만 connect를 허용한다.

            String authorizationHeader = String.valueOf(accessor.getNativeHeader("Authorization").get(0));
            //token 인증
            String email =validateToken(authorizationHeader);
            //헤더에 user email 추가
            Map<String,Object> sessionAttributes = accessor.getSessionAttributes();
            sessionAttributes.put("email",email);
            accessor.setSessionAttributes(sessionAttributes);

        }else if(accessor.getCommand().equals(StompCommand.SUBSCRIBE)){
            //destination 경로 검사 "/app/chat/room/**","/topic/room/**"이외의 경로 구독은 허용하지 않는다.
            String destination = accessor.getDestination();
            if (destination == null ||!destination.startsWith("/topic/room/")) {
                throw new MessageDeliveryException("Invalid destination");
            }
            //방인원 검사
            String email=Optional.of((String) accessor.getSessionAttributes().get("email"))
                    .orElseThrow(()->new MessageDeliveryException("Invalid email"));
            Long roomId = Long.parseLong(destination.split("/")[3]) ;
            socketService.join(roomId, email);
        }else if(accessor.getCommand().equals(StompCommand.MESSAGE)){
            throw new MessageDeliveryException("Invalid command");
        }

        System.out.println("Command: " + accessor.getCommand());
        MessageHeaders headers = message.getHeaders();
        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
        if (multiValueMap != null) {
            for (Map.Entry<String, List<String>> head : multiValueMap.entrySet()) {
                System.out.println(head.getKey() + "#" + head.getValue());
            }
        }
        return message;
    }
    /* 토큰 인증
     * 메세지 헤더에 존재하는 "Authorization"을 받는다.
     * 받은 값을 검증한다.
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
}
