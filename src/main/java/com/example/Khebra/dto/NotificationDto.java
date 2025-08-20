package com.example.Khebra.dto;

import com.example.Khebra.entity.TypeNotification;
import lombok.*;

import java.util.Date;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private int id;
    private String senderName;
    private String senderImageUrl;
    private String message;
    private Date timestamp;
    private TypeNotification typeNotification;
}
