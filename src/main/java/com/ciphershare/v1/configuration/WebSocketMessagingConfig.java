package com.ciphershare.v1.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ciphershare.v1.service.NotificationHandler;

@Configuration
@EnableWebSocket
public class WebSocketMessagingConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new NotificationHandler(),"/ws/notifications").setAllowedOrigins("*");
    }

}
