package com.example.Khebra.repository;

import com.example.Khebra.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> getAllByReceiverEmail(String Email, Pageable pageable);
}
