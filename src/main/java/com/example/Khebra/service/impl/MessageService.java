package com.example.Khebra.service.impl;

import com.example.Khebra.dto.MessageDto;
import com.example.Khebra.entity.Message;
import com.example.Khebra.repository.ConversationRepository;
import com.example.Khebra.repository.MessageRepository;
import com.example.Khebra.service.IMessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MessageService implements IMessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public static MessageDto mapToDto(Message message) {
        return MessageDto.builder()
                .messageId(message.getId())
                .text(message.getContent())
                .timestamp(message.getTimestamp())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .conversationId(message.getConversation().getId())
                .build();
    }


    @Override
    public MessageDto getLatestMessage(int conversationId) {
        Message message = messageRepository.findTopByConversationIdOrderByTimestampDesc(conversationId);
        return mapToDto(message);

    }

    @Override
    public MessageDto sendMessage(MessageDto dto) {
        Message message = new Message();
        message.setContent(dto.getText());
        message.setSenderId(dto.getSenderId());
        message.setReceiverId(dto.getReceiverId());
        message.setTimestamp(LocalDateTime.now());
        message.setConversation(conversationRepository.findById(dto.getConversationId()).get());

        Message saved = messageRepository.save(message);
        System.out.println("message saved " + saved);
        MessageDto result = mapToDto(saved); // Map back to DTO

        String receiverDestination = "/topic/conversations/" + dto.getReceiverId();
        messagingTemplate.convertAndSend(receiverDestination, result);

        // Optional: send to sender too (for echo/backlog confirmation)


        return result;
    }

}
