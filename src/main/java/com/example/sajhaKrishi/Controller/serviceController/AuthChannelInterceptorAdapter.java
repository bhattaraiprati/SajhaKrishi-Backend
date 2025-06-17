package com.example.sajhaKrishi.Controller.serviceController;

import com.example.sajhaKrishi.Services.JWTService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Collections;
import java.util.List;



@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    private final JWTService jwtService;
    public AuthChannelInterceptorAdapter(JWTService jwtService) {
        this.jwtService = jwtService;
    }
@Override
public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    // Debug: Print all intercepted commands
    System.out.println("=== WebSocket Interceptor Debug ===");
    System.out.println("Channel: " + channel.toString());
    System.out.println("Accessor: " + accessor);

    if (accessor != null) {
        System.out.println("Command: " + accessor.getCommand());
        System.out.println("Destination: " + accessor.getDestination());
        System.out.println("Session ID: " + accessor.getSessionId());
        System.out.println("User: " + accessor.getUser());
        System.out.println("Native Headers: " + accessor.toNativeHeaderMap());
    }
    System.out.println("Here is the accessor"+ accessor);
    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
        String token = extractToken(accessor);
        System.out.println("Here is the tokken"+ token);


        if (token != null) {
            try {
                // Validate token using your existing JWTService
                Claims claims = jwtService.extractAllClaims(token);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                String name = claims.get("name", String.class);
                Long userId = claims.get("id", Long.class);

                // Create authorities
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
                );

                System.out.println("Here is the authorities..."+ authorities);



                // Create authentication object
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Create custom principal with user details
                Principal principal = new CustomPrincipal(username, name, userId, role);
                accessor.setUser(principal);

//                log.info("WebSocket connection authenticated for user: {}", username);

            } catch (Exception e) {
//                log.error("JWT validation failed: ", e);
                throw new RuntimeException("Invalid JWT token", e);
            }
        } else {
//            log.error("No JWT token found in WebSocket connection");
            throw new RuntimeException("No JWT token found");
        }
    }

    return message;
}
    private String extractToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static class CustomPrincipal implements Principal {
        private final String username;
        private final String name;
        private final Long userId;
        private final String role;

        public CustomPrincipal(String username, String name, Long userId, String role) {
            this.username = username;
            this.name = name;
            this.userId = userId;
            this.role = role;
        }

        @Override
        public String getName() {
            return username;
        }

        public String getDisplayName() {
            return name;
        }

        public Long getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }
    }

}
