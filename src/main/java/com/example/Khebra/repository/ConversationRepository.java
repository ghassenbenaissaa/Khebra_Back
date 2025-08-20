package com.example.Khebra.repository;

import com.example.Khebra.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {


    @Query("""
    SELECT c FROM Conversation c
    WHERE c.participant1Id = :id OR c.participant2Id = :id
    ORDER BY (
        SELECT MAX(m.timestamp)
        FROM Message m
        WHERE m.conversation.id = c.id
    ) DESC NULLS LAST
    """)
    Page<Conversation> findAllByUserIdOrderByLatestMessageDesc(@Param("id") int id, Pageable pageable);

}
