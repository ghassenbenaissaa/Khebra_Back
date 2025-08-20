package com.example.Khebra.service;

import com.example.Khebra.dto.NotificationDto;
import com.example.Khebra.entity.Notification;
import org.springframework.data.domain.Page;

import java.util.List;

public interface INotificationService {

    void sendNotification(String email, Notification notification);
    Page<NotificationDto> getNotification(int page, int size, String expertEmail);

}
