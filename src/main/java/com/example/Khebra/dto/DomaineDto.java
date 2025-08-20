package com.example.Khebra.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomaineDto {
    private int id;
    private String name;
    private String imageUrl;
    private int imageId;
}
