package com.example.Khebra.service;

import com.example.Khebra.dto.AvisAdminDto;
import com.example.Khebra.dto.AvisDto;
import com.example.Khebra.dto.AvisRequest;
import com.example.Khebra.entity.*;
import com.example.Khebra.repository.AvisRepository;
import com.example.Khebra.repository.ClientRepository;
import com.example.Khebra.repository.DemandeCommunicationRepository;
import com.example.Khebra.repository.ExpertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AvisService implements IAvisService {

    private final AvisRepository avisRepository;
    private final ExpertRepository expertRepository;
    private final ClientRepository clientRepository;
    private final INotificationService notificationService;
    private final DemandeCommunicationRepository demandRepository;

    @Override
    public List<AvisDto> getAvis(int id) {
        List<Avis> avisList = avisRepository.findByExpertIdAndEnabledTrue(id);
        return avisList.stream()
                .map(this::mapToAvisDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AvisAdminDto> getAllAvis(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("dateReview").descending());

            Page<Avis> avisPage = avisRepository.findAll(pageable);

            if (avisPage.isEmpty()) {
                throw new RuntimeException("Aucun avis trouvé.");
            }

            return avisPage.map(this::mapToAvisAdminDto);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des avis : " + e.getMessage());
        }
    }


    @Override
    public void addAvis(AvisRequest avis) {
        DemandeCommunication demande = demandRepository
                .findById(avis.getDemandeId())
                .orElseThrow(() -> new RuntimeException("Aucune demande de communication trouvée entre le client et l'expert."));

        Expert expert = demande.getExpert();
        Client client = demande.getClient();

        if (demande.getStatusDemande() != StatusDemande.ACHEVÉE) {
            throw new RuntimeException("La demande de communication n'a pas encore été acceptée.");
        }

        Conversation conversation = demande.getConversation();
        if (conversation == null || conversation.getStatus() != StatusConversation.ACHEVÉE) {
            throw new RuntimeException("La conversation n'est pas encore terminée.");
        }

        Avis newAvis = new Avis();
        newAvis.setClient(client);
        newAvis.setExpert(expert);
        newAvis.setComment(avis.getComment());
        newAvis.setDateReview(LocalDateTime.now());
        newAvis.setRate(avis.getRating());
        newAvis.setEnabled(true);

        avisRepository.save(newAvis);
        demande.setStatusDemande(StatusDemande.COMMENTÉE);
        demandRepository.save(demande);
        updateRate(expert.getId());

    }


    @Override
    public void disableAvis(int id) {
        Avis avis = avisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avis introuvable avec l'identifiant : " + id));

        avis.setEnabled(false);
        avisRepository.save(avis);

        Expert expert = avis.getExpert();
        if (expert != null) {
            updateRate(expert.getId());
        } else {
            throw new RuntimeException("Expert introuvable pour l’avis donné.");
        }
    }

    @Override
    public void enableAvis(int id) {
        Avis avis = avisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avis introuvable avec l'identifiant : " + id));

        avis.setEnabled(true);
        avisRepository.save(avis);

        Expert expert = avis.getExpert();
        if (expert != null) {
            updateRate(expert.getId());
        } else {
            throw new RuntimeException("Expert introuvable pour l’avis donné.");
        }
    }


    @Override
    public void updateRate(int expertId) {
        List<Avis> avisList = avisRepository.findByExpertIdAndEnabledTrue(expertId);

        double averageRate = avisList.isEmpty()
                ? 0.0
                : avisList.stream()
                .mapToDouble(Avis::getRate)
                .average()
                .orElse(0.0);

        Expert expert = expertRepository.findById(expertId)
                .orElseThrow(() -> new RuntimeException("Expert introuvable avec l'identifiant : " + expertId));

        expert.setRating(averageRate);
        expertRepository.save(expert);
    }


    public AvisDto mapToAvisDto(Avis avis) {

        return AvisDto.builder()
                .id(avis.getId())
                .DateReview(avis.getDateReview())
                .expertId(avis.getExpert().getId())
                .clientFullName(avis.getClient().getFullName())
                .clientImageUrl(avis.getClient().getImage().getImageUrl())
                .Comment(avis.getComment())
                .build();

    }

    public AvisAdminDto mapToAvisAdminDto(Avis avis) {

        return AvisAdminDto.builder()
                .id(avis.getId())
                .DateReview(avis.getDateReview())
                .expertId(avis.getExpert().getId())
                .clientId(avis.getClient().getId())
                .clientFullName(avis.getClient().getFullName())
                .expertFullName(avis.getExpert().getFullName())
                .isClientBanned(avis.getClient().isBanned())
                .isExpertBanned(avis.getExpert().isBanned())
                .enabled(avis.isEnabled())
                .Comment(avis.getComment())
                .Rate(avis.getRate())
                .build();

    }
}
