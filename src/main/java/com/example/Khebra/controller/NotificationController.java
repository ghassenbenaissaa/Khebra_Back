package com.example.Khebra.controller;

import com.example.Khebra.dto.NotificationDto;
import com.example.Khebra.entity.User;
import com.example.Khebra.service.INotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("notification")
@AllArgsConstructor
public class NotificationController {


    private final INotificationService notificationService;
    @GetMapping("/getAll")
    public ResponseEntity<Page<NotificationDto>> getNotifications(@RequestParam int page,
                                                                  @RequestParam int size,
                                                                  @AuthenticationPrincipal User user) {
        Page<NotificationDto> notificationsPage = notificationService.getNotification(page, size, user.getEmail());
        return ResponseEntity.ok(notificationsPage);
    }
}
