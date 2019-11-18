package com.mvcproject.mvcproject.config;

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
                .simpDestMatchers("/app/**").hasAuthority("USER")
                .simpSubscribeDestMatchers("/user/**", "/queue/**").hasAuthority("USER")
                .simpSubscribeDestMatchers("/user/**", "/queue/**").hasAuthority("ADMIN")
                .anyMessage().denyAll();
    }
}
