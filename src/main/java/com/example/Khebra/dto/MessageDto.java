package com.example.Khebra.dto;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private int messageId;
    private LocalDateTime timestamp;
    private String text;
    private int senderId;
    private int receiverId;
    private int conversationId;
}
