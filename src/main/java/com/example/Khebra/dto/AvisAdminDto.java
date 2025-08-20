package com.example.Khebra.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvisAdminDto {
    private int id;
    private String clientFullName;
    private String expertFullName;
    private int clientId;
    private int expertId;
    private int Rate;
    private String Comment;
    private LocalDateTime DateReview;
    private boolean enabled = true;
    private boolean isClientBanned;
    private boolean isExpertBanned;
}
