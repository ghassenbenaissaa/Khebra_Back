package com.example.Khebra.service.impl;

import com.example.Khebra.dto.NotificationDto;
import com.example.Khebra.entity.Notification;
import com.example.Khebra.repository.ClientRepository;
import com.example.Khebra.repository.NotificationRepository;
import com.example.Khebra.repository.UserRepository;
import com.example.Khebra.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void sendNotification(String email, Notification notification) {

        messagingTemplate.convertAndSend("/queue/notification/"+email, notification);
        notificationRepository.save(notification);
        log.info("Notification sent to {} with payload {}", email, notification);
    }

    @Override
    public Page<NotificationDto> getNotification(int page, int size, String email) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        Page<Notification> notificationsPage = notificationRepository.getAllByReceiverEmail(email, pageable);

        return notificationsPage.map(this::MapToNotificationDto);
    }

    private NotificationDto MapToNotificationDto(Notification notification) {
            return NotificationDto.builder()
                    .id(notification.getId())
                    .senderName(userRepository.findUserByEmail(notification.getSenderEmail()).getFullName())
                    .senderImageUrl(userRepository.findUserByEmail(notification.getSenderEmail()).getImage().getImageUrl())
                    .message(notification.getMessage())
                    .timestamp(notification.getTimestamp())
                    .build();
    }

}