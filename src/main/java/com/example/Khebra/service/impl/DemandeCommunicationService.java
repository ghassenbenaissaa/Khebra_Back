package com.example.Khebra.service.impl;

import com.example.Khebra.dto.DemandeCommunicationResponse;
import com.example.Khebra.entity.*;
import com.example.Khebra.exception.DemandeAlreadyExistsException;
import com.example.Khebra.projections.StatusCountProjection;
import com.example.Khebra.repository.ClientRepository;
import com.example.Khebra.repository.ConversationRepository;
import com.example.Khebra.repository.DemandeCommunicationRepository;
import com.example.Khebra.repository.ExpertRepository;
import com.example.Khebra.service.IDemandeCommunicationService;
import com.example.Khebra.service.INotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeCommunicationService implements IDemandeCommunicationService {


    private final DemandeCommunicationRepository demandeRepo;
    private final ClientRepository clientRepo;
    private final ExpertRepository expertRepo;
    private final INotificationService notificationService;
    private final MessageService messageService;
    private final ConversationRepository conversationRepo;


    @Override
    public List<DemandeCommunicationResponse> getDemandeCommunication(String email) {
        List<DemandeCommunication> demandes = demandeRepo.findByClientEmail(email);
        return demandes.stream()
                .map(demande -> new DemandeCommunicationResponse(
                        demande.getExpert().getEmail(), // or demande.getExpertEmail() if stored directly
                        demande.getTimestamp(),
                        demande.getMessage(),
                        demande.getStatusDemande(),
                        demande.getId(),
                        demande.getConversation() != null ? demande.getConversation().getId() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DemandeCommunicationResponse> getDemandeCommunicationForExpert(String email) {
        Expert expert = expertRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Expert with email " + email + " not found"));

        List<DemandeCommunication> demandes = demandeRepo.findByExpert(expert);

        return demandes.stream()
                .map(demande -> new DemandeCommunicationResponse(
                        demande.getClient() != null ? demande.getClient().getEmail() : "Unknown",
                        demande.getTimestamp(),
                        demande.getMessage(),
                        demande.getStatusDemande(),
                        demande.getId(),
                        demande.getConversation() != null ? demande.getConversation().getId() : null
                ))
                .collect(Collectors.toList());
    }



    public void createDemandeCommunication(String clientEmail, String expertEmail, String message) {
        Client client = clientRepo.findByEmail(clientEmail)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Expert expert = expertRepo.findByEmail(expertEmail)
                .orElseThrow(() -> new RuntimeException("Expert not found"));

        DemandeCommunication demande = new DemandeCommunication();

        Optional<DemandeCommunication> existing = demandeRepo
                .findByClientAndExpertAndStatusDemande(client, expert, StatusDemande.EN_ATTENTE);

        if (existing.isPresent()) {
            throw new DemandeAlreadyExistsException("You already have a pending request with this expert.");
        }else {

            demande.setTimestamp(LocalDateTime.now());
            demande.setStatusDemande(StatusDemande.EN_ATTENTE);
            demande.setClient(client);
            demande.setExpert(expert);
            demande.setMessage(message);

            notificationService.sendNotification(expertEmail,Notification.builder()
                    .message(client.getFullName()+" vous a evnoyer une demande de communication")
                    .senderEmail(client.getEmail())
                    .receiverEmail(expert.getEmail())
                    .timestamp(new Date())
                    .typeNotification(TypeNotification.DEMANDE)
                    .build());

            demandeRepo.save(demande);
        }
    }

    public void updateDemandeCommunicationStatus(String expert_email, String status) {

        DemandeCommunication demande = demandeRepo.findByExpertEmail(expert_email)
                .orElseThrow(() -> new RuntimeException("Demande not found"));

        StatusDemande statusEnum = Arrays.stream(StatusDemande.values())
                .filter(e -> e.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid status: " + status));

        if (!statusEnum.equals(StatusDemande.EN_ATTENTE)) {
            demande.setStatusDemande(statusEnum);
        }
        demandeRepo.save(demande);
    }


    public void deleteDemandeCommunication(String client_email,String expert_email,String status) {
        DemandeCommunication demande = demandeRepo.findByClientEmailAndExpertEmail(client_email,expert_email)
                .orElseThrow(() -> new RuntimeException("Demande not found"));

        StatusDemande statusEnum = Arrays.stream(StatusDemande.values())
                .filter(e -> e.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid status: " + status));

        if (statusEnum.equals(StatusDemande.EN_ATTENTE)) {
            demandeRepo.delete(demande);
        }


    }

    @Override
    public Map<StatusDemande, Long> getDemandeCountsByStatus(int id) {
        List<StatusCountProjection> projections = demandeRepo.countByStatusForExpert(id);

        Map<StatusDemande, Long> result = new EnumMap<>(StatusDemande.class);
        for (StatusCountProjection p : projections) {
            result.put(p.getStatus(), p.getCount());
        }

        // Fill in missing statuses with 0
        for (StatusDemande s : StatusDemande.values()) {
            result.putIfAbsent(s, 0L);
        }

        return result;
    }

    @Override
    public void StatusDemande(int demandeId, StatusDemande statusDemande) {
        DemandeCommunication demande = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new EntityNotFoundException("Demande with ID " + demandeId + " not found"));
        if (statusDemande == StatusDemande.ACCEPTEE) {
            demande.setStatusDemande(StatusDemande.ACCEPTEE);
            demandeRepo.save(demande);
            Client client = clientRepo.findById(demande.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            Expert expert = expertRepo.findById(demande.getExpert().getId())
                    .orElseThrow(() -> new RuntimeException("Expert not found"));
            // 2. Create Conversation
            Conversation conversation = new Conversation();
            conversation.setStatus(StatusConversation.EN_COURS);
            conversation.setParticipant1Id(client.getId());
            conversation.setParticipant2Id(expert.getId());
            // 3. Link Demande and Conversation bidirectionally
            demande.setConversation(conversation);
            conversation.setDemandeCommunication(demande);
            conversation = conversationRepo.save(conversation);

            // 4. Create initial Message entity from demande message
            Message initialMessage = new Message();
            initialMessage.setContent("Bonjour " + client.getFullName() + ", comment puis-je vous aider ?");
            initialMessage.setTimestamp(LocalDateTime.now());
            initialMessage.setConversation(conversation);
            initialMessage.setSenderId(expert.getId());
            initialMessage.setReceiverId(client.getId());
            messageService.sendMessage(MessageService.mapToDto(initialMessage));
            // 5. Add this Message to Conversation's messages list


            notificationService.sendNotification(demande.getClient().getEmail(), Notification.builder()
                    .message("Votre demande de communication avec l'expert " + demande.getExpert().getFullName() + " a été acceptée.")
                    .receiverEmail(demande.getClient().getEmail())
                    .senderEmail(demande.getExpert().getEmail())
                    .timestamp(new Date())
                    .typeNotification(TypeNotification.DEMANDE)
                    .build());

        } else if (statusDemande == StatusDemande.REFUSEE) {
            demande.setStatusDemande(StatusDemande.REFUSEE);
            demandeRepo.save(demande);
            notificationService.sendNotification(demande.getClient().getEmail(), Notification.builder()
                    .message("Votre demande de communication avec l'expert " + demande.getExpert().getFullName() + " a été refusée.")
                    .receiverEmail(demande.getClient().getEmail())
                    .senderEmail(demande.getExpert().getEmail())
                    .timestamp(new Date())
                    .typeNotification(TypeNotification.DEMANDE)
                    .build());
        }
    }



}
