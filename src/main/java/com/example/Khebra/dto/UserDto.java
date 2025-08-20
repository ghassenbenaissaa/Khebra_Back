package com.example.Khebra.dto;

import com.example.Khebra.entity.Image;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String adresse;
    private String numTel;
    private String cin;
    private ImageDto image;
    private String interet;
    public String expertise;
    public String domaineExpertise;
    public String biographie;
    public String rating;
    public List<String> role;

}
