package com.example.Khebra.dto;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvisDto {
    private int id;
    private String clientFullName;
    private String clientImageUrl;
    private int expertId;
    private String Comment;
    private LocalDateTime DateReview;

}
