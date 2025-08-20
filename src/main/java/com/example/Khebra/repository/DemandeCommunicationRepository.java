package com.example.Khebra.repository;

import com.example.Khebra.dto.DemandeCommunicationResponse;
import com.example.Khebra.entity.Client;
import com.example.Khebra.entity.DemandeCommunication;
import com.example.Khebra.entity.Expert;
import com.example.Khebra.entity.StatusDemande;
import com.example.Khebra.projections.StatusCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DemandeCommunicationRepository extends JpaRepository<DemandeCommunication, Integer> {
    Optional<DemandeCommunication> findByClientAndExpertAndStatusDemande(Client client, Expert expert, StatusDemande statusDemande);
    Optional<DemandeCommunication> findByExpertEmail(String expert_email);
    Optional<DemandeCommunication> findByClientEmailAndExpertEmail(String client_email,String expert_email);
    List<DemandeCommunication> findByClientEmail(String email);
    @Query("SELECT d.statusDemande AS status, COUNT(d) AS count FROM DemandeCommunication d WHERE d.expert.id = :id GROUP BY d.statusDemande")
    List<StatusCountProjection> countByStatusForExpert(@Param("id") int id);
    Optional<DemandeCommunication> findByClientIdAndExpertId(int clientId, int expertId);
    List<DemandeCommunication> findByExpert(Expert expert);
    DemandeCommunication findByConversationId(int conversationId);


}
