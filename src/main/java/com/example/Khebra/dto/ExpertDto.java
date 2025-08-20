package com.example.Khebra.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertDto {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String adresse;
    private String numTel;
    private String cin;
    private ImageDto image;
    public String expertise;
    public String domaineExpertise;
    public String biographie;
    public Double rating;
    public String role;
    public boolean isValidated;
    public boolean isBanned;
}
