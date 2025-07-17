package com.example.sajhaKrishi.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple message broker for destinations starting with /topic and /queue
        registry.enableSimpleBroker("/topic", "/queue");

        // Set application destination prefix
        registry.setApplicationDestinationPrefixes("/app");

        // Enable user-specific destinations
        registry.setUserDestinationPrefix("/user");

    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint
        registry.addEndpoint("/ws" ) // connection establishment
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

}
