package com.example.Khebra.service;

import com.example.Khebra.dto.DemandeCommunicationResponse;
import com.example.Khebra.entity.StatusDemande;

import java.util.List;
import java.util.Map;

public interface IDemandeCommunicationService {


    List<DemandeCommunicationResponse> getDemandeCommunication(String email); //both client and expert
    void createDemandeCommunication(String clientEmail, String expertEmail, String message); //token of authenticated
    void StatusDemande(int demandeId, StatusDemande statusDemande);
    List<DemandeCommunicationResponse> getDemandeCommunicationForExpert(String email);

    void updateDemandeCommunicationStatus(String expert_email, String status); //experts token simply changing the statusDemande

    void deleteDemandeCommunication(String client_email,String expert_email,String status);//when you look for the demande
    // you will get expert_email
    Map<StatusDemande, Long> getDemandeCountsByStatus(int id);
}
