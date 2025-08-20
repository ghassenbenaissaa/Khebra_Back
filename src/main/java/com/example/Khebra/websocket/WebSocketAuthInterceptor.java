package com.example.Khebra.websocket;

import com.example.Khebra.security.JwtService;
import com.example.Khebra.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                // Retrieve token from handshake attributes
                String token = (String) accessor.getSessionAttributes().get("token");
                System.out.println("Processing CONNECT command, token present: " + (token != null));

                if (token != null && !token.trim().isEmpty()) {
                    // Extract username first
                    String username = jwtService.extractUsername(token);
                    System.out.println("Extracted username: " + username);

                    // Then validate the token with the username
                    if (username != null && jwtService.isTokenValid(token, userDetailsService.loadUserByUsername(username))) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                        accessor.setUser(auth);
                        System.out.println("User authenticated and set: " + username + " (Principal: " + auth.getName() + ")");
                    } else {
                        System.out.println("Invalid token for username: " + username);
                    }
                } else {
                    System.out.println("No token found in session attributes");
                }
            } catch (Exception e) {
                System.out.println("Error during WebSocket authentication: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return message;
    }
}