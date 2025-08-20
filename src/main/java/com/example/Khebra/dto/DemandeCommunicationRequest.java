package com.example.Khebra.dto;

import com.example.Khebra.entity.StatusDemande;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeCommunicationRequest {

    private String expertEmail;
    private LocalDateTime timestamp;
    private String message;
    private StatusDemande status;
}
