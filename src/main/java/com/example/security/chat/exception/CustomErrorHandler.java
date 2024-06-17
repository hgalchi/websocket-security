package com.example.security.chat.exception;

import com.example.security.codes.ErrorCode;
import com.example.security.codes.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
@Log4j2
public class CustomErrorHandler extends StompSubProtocolErrorHandler {

    public CustomErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        log.info("CustomErrorHandler exception : " + ex);

        return super.handleClientMessageProcessingError(clientMessage, ex);

    }

    private Message<byte[]> errorMessage(String message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        accessor.setMessage(ErrorCode.UNAUTHORIZED.toString());
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.toString().getBytes(StandardCharsets.UTF_8)
                , accessor.getMessageHeaders());
    }

}
