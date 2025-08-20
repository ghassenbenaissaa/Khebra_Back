package com.example.Khebra.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private String token;
    public String userId;
    public Boolean isValidated;
    public boolean isBanned;
    public boolean isActive;
    public String role;
    private String refreshToken;

}
