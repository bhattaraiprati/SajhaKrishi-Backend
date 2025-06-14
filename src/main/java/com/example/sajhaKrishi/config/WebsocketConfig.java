package com.example.sajhaKrishi.config;

import com.example.sajhaKrishi.Controller.serviceController.AuthChannelInterceptorAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple message broker for destinations starting with /topic and /queue
        registry.enableSimpleBroker("/topic");

        // Set application destination prefix
        registry.setApplicationDestinationPrefixes("/app");


    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint
        registry.addEndpoint("/chat" ) // connection establishment
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();

//        stompEndpointRegistry.addEndpoint("/chat/websocket")
//                .setAllowedOriginPatterns("http://localhost:5173");
    }

//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> messageConverters){
//        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
//        resolver.setDefaultMimeType(APPLICATION_JSON);
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setObjectMapper(new ObjectMapper());
//        converter.setContentTypeResolver(resolver);
//        messageConverters.add(converter);
//
//        return false;
//    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(authChannelInterceptorAdapter);
//    }

}
