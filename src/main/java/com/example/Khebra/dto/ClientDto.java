package com.example.Khebra.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String adresse;
    private String numTel;
    private String cin;
    private ImageDto image;
    private String interet;
    public String rating;
    public String role;
    public boolean isBanned;
}
