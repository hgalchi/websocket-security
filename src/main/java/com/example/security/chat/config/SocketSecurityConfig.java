package com.example.security.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class SocketSecurityConfig  {
    @Bean
    public MessageMatcherDelegatingAuthorizationManager.Builder messageMatcherDelegatingAuthorizationManagerBuilder() {
        return new MessageMatcherDelegatingAuthorizationManager.Builder();
    }
// todo : stomp에 security 적용 이슈  interceptor로 구현하고 추후 구현
    /*@Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                //구독 Destionation 접근권한제한
                .simpSubscribeDestMatchers("/topic/room/**").permitAll()
                .simpDestMatchers("/app/**").hasRole("CUSTOMER")
                .simpTypeMatchers(SimpMessageType.SUBSCRIBE).authenticated()
                .simpTypeMatchers(SimpMessageType.MESSAGE).denyAll()
                .anyMessage().denyAll();

        return messages.build();
    }*/

}
