package com.example.Khebra.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "Firstname is mandatory")
    @NotBlank(message = "Firstname is mandatory")
    private String firstname;

    @NotEmpty(message = "Lastname is mandatory")
    @NotBlank(message = "Lastname is mandatory")
    private String lastname;

    @Email(message = "Email is not formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String password;

    @NotEmpty(message = "numTel is mandatory")
    @NotBlank(message = "numTel is mandatory")
    @Size(min = 8, message = "Numero tel doit contenir au moins 8 charactere.")
    private String numTel;

    @NotEmpty(message = "CIN is mandatory")
    @NotBlank(message = "CIN is mandatory")
    private String cin;

    private String address;


    private String userType;
    private String point;
    //Expert
    private String expertise;
    private Integer DomaineId;
    private String biographie;

    //Client
    private String interet;
}
