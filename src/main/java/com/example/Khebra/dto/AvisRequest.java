package com.example.Khebra.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AvisRequest {
        private int demandeId;
        private String comment;
        private int rating;
}
