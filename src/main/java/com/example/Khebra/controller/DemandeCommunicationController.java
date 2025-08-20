package com.example.Khebra.controller;

import com.example.Khebra.dto.DemandeCommunicationRequest;
import com.example.Khebra.dto.DemandeCommunicationResponse;
import com.example.Khebra.entity.StatusDemande;
import com.example.Khebra.entity.User;
import com.example.Khebra.exception.DemandeAlreadyExistsException;
import com.example.Khebra.exception.ExpertNotFoundException;
import com.example.Khebra.service.IDemandeCommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("demande-communication")
@RequiredArgsConstructor

public class DemandeCommunicationController {

    private final IDemandeCommunicationService demandeService;

    @GetMapping("/")
    public List<DemandeCommunicationResponse> getDemandeCommunication(@AuthenticationPrincipal User authenticatedUser)  {
        String email = authenticatedUser.getUsername();
       return (demandeService.getDemandeCommunication(email));
    }

    @GetMapping("/expert")
    public List<DemandeCommunicationResponse> getDemandeCommunicationForExpert(@AuthenticationPrincipal User authenticatedUser) {
        String email = authenticatedUser.getUsername();
        return demandeService.getDemandeCommunicationForExpert(email);
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateDemandeStatus(@RequestParam int id, @RequestParam StatusDemande status) {
        demandeService.StatusDemande(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/")
    public ResponseEntity<?> createDemande(@RequestBody DemandeCommunicationRequest request,
                                           @AuthenticationPrincipal User authenticatedUser) {
        try {
            String clientEmail = authenticatedUser.getUsername(); //username is email
            demandeService.createDemandeCommunication(clientEmail, request.getExpertEmail(), request.getMessage());
            return ResponseEntity.ok(Collections.singletonMap("message", "Demande sent successfully"));

        } catch (DemandeAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (ExpertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong on the server");
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateDemande(@RequestBody DemandeCommunicationRequest request,
                                           @AuthenticationPrincipal User authenticatedUser) {
        try{
            String ExpertEmail = authenticatedUser.getUsername();
            demandeService.updateDemandeCommunicationStatus(ExpertEmail, request.getStatus().toString());
            return ResponseEntity.ok(Collections.singletonMap("message", "Demande updated successfully"));

        }catch (ExpertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong on the server");
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteDemande(@RequestBody DemandeCommunicationRequest request
                                           ,@AuthenticationPrincipal User authenticatedUser) {

            String client_email = authenticatedUser.getUsername();
            demandeService.deleteDemandeCommunication(client_email, request.getExpertEmail(), request.getStatus().toString());
            return ResponseEntity.ok(Collections.singletonMap("message", "Demande deleted successfully"));

    }

    @GetMapping("/status-counts")
    public Map<StatusDemande, Long> getCountsDemande(@RequestParam int id) {
        return demandeService.getDemandeCountsByStatus(id);
    }

}
