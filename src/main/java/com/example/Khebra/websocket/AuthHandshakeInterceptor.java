package com.example.Khebra.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger logger = Logger.getLogger(AuthHandshakeInterceptor.class.getName());

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        try {
            URI uri = request.getURI();
            String query = uri.getQuery();

            if (query != null) {
                Map<String, String> queryParams = parseQueryString(query);
                String token = queryParams.get("token");
                logger.info("WebSocket handshake URL: " + uri);

                if (token != null && !token.isEmpty()) {
                    // Decode the token in case it's URL encoded
                    token = URLDecoder.decode(token, StandardCharsets.UTF_8);
                    attributes.put("token", token);
                    System.out.println("Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");
                } else {
                    System.out.println("No token found in query parameters");
                }
            } else {
                System.out.println("No query string found in URI: " + uri.toString());
            }
        } catch (Exception e) {
            System.out.println("Error extracting token: " + e.getMessage());
            return false;
        }

        return true;
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {
        if (ex != null) {
            System.out.println("Handshake failed: " + ex.getMessage());
        } else {
            System.out.println("Handshake completed successfully");
        }
    }
}