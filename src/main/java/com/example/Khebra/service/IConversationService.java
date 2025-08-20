package com.example.Khebra.service;

import com.example.Khebra.dto.ConversationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IConversationService {

    Page<ConversationDto> GetAllConversation(int userId,int page, int size);
    ConversationDto GetConversation(int conversationId);
    void UpdateConversationStatus(int conversationId);

}
