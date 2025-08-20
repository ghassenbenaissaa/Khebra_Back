package com.example.Khebra.service.impl;

import com.example.Khebra.dto.ConversationDto;
import com.example.Khebra.dto.MessageDto;
import com.example.Khebra.entity.*;
import com.example.Khebra.repository.*;
import com.example.Khebra.service.IConversationService;
import com.example.Khebra.service.IMessageService;
import com.example.Khebra.service.INotificationService;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConversationService implements IConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final DemandeCommunicationRepository demandeCommunicationRepository;
    private final IMessageService messageService;
    private final INotificationService notificationService;
    private final ClientRepository clientRepository;
    private final ExpertRepository expertRepository;


    @Override
    public Page<ConversationDto> GetAllConversation(int userId, int page, int size) {
        // Create pageable with sorting by createdAt ascending
        Pageable sortedPageable = PageRequest.of(page, size);

        Page<Conversation> conversations = conversationRepository.findAllByUserIdOrderByLatestMessageDesc(userId, sortedPageable);

        return conversations.map(conversation -> {
            ConversationDto dto = mapToDto(conversation);
            MessageDto latestMessage = messageService.getLatestMessage(conversation.getId());

            dto.setMessages(latestMessage != null ? List.of(latestMessage) : List.of());
            return dto;
        });
    }

    @Override
    public void UpdateConversationStatus(int conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        conversation.setStatus(StatusConversation.ACHEVÉE);
        conversationRepository.save(conversation);
        DemandeCommunication demandeCommunication = demandeCommunicationRepository.findByConversationId(conversationId);
        if (demandeCommunication != null) {
            demandeCommunication.setStatusDemande(StatusDemande.ACHEVÉE);
            demandeCommunicationRepository.save(demandeCommunication);
        }
        Client client = clientRepository.findById(conversation.getParticipant1Id()).orElseThrow(() -> new IllegalArgumentException("Client not found"));
        Expert expert = expertRepository.findById(conversation.getParticipant2Id()).orElseThrow(() -> new IllegalArgumentException("Expert not found"));
        notificationService.sendNotification(client.getEmail(),Notification.builder()
                .message("La conversation avec l'expert " + expert.getFullName() + " a été clôturée. Vous pouvez désormais laisser un avis pour évaluer la qualité de cette communication.")
                .receiverEmail(client.getEmail())
                .senderEmail(expert.getEmail())
                .timestamp(new Date())
                .typeNotification(TypeNotification.DEMANDE)
                .build());
    }

    @Override
    public ConversationDto GetConversation(int conversationId) {

        return conversationRepository.findById(conversationId).map(this::mapToDto).orElse(null);
    }

    private ConversationDto mapToDto(Conversation conversation
    ) {
        List<MessageDto> messageDto = conversation.getMessages().stream()
                .map(MessageService::mapToDto)
                .collect(Collectors.toList());
        User participant1 = userRepository.findById(conversation.getParticipant1Id()).orElse(null);

        User participant2 = userRepository.findById(conversation.getParticipant2Id()).orElse(null);

        return ConversationDto.builder()
                .id(conversation.getId())
                .participant1FullName(participant1.getFullName())
                .participant2FullName(participant2.getFullName())
                .participant1Id(conversation.getParticipant1Id())
                .participant2Id(conversation.getParticipant2Id())
                .participant1ImageUrl(participant1.getImage().getImageUrl())
                .participant2ImageUrl(participant2.getImage().getImageUrl())
                .messages(messageDto)
                .build();
    }
}