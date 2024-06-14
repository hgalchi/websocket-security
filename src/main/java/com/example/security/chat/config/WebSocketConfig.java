package com.example.security.chat.config;

import com.example.security.chat.ChatroomInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChannelInterceptor chatroomInterceptor;
    private final StompSubProtocolErrorHandler customErrorHandler;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket")
                .setAllowedOrigins("*");
        registry.setErrorHandler(customErrorHandler);
        //.withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        //subscriptions,Broadcastin기능을 제공, "destination"헤더를 가진 메세지를 브로커로 라우팅
        config.enableSimpleBroker("/topic","/queue");
        //stomp메세지의 "destination"헤더는 @MessageMapping으로 메서드 라우팅
        config.setApplicationDestinationPrefixes("/app");
        //특정 유저와 메세지를 주고 받을 수 있는 prefix설정,default "/user"
        config.setUserDestinationPrefix("/user");
    }

    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatroomInterceptor);
    }

}
