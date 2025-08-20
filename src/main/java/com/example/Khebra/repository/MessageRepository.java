package com.example.Khebra.repository;

import com.example.Khebra.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    Message findTopByConversationIdOrderByTimestampDesc(int conversationId);

}
