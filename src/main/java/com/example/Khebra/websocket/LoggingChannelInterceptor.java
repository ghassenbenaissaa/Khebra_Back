package com.example.Khebra.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class LoggingChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            String command = accessor.getCommand() != null ? accessor.getCommand().name() : "N/A";
            String destination = accessor.getDestination();
            System.out.println("[STOMP] Command: " + command + ", Destination: " + destination);

        }
        return message;
    }
}

