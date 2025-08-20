package com.example.Khebra.controller;

import com.example.Khebra.dto.AuthenticationRequest;
import com.example.Khebra.dto.AuthenticationResponse;
import com.example.Khebra.dto.RegistrationRequest;
import com.example.Khebra.service.impl.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
    @RequestBody @Valid AuthenticationRequest request) throws MessagingException {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/activate-account")
    public void activateAccount(
            @RequestParam String token) throws MessagingException {
        service.activateAccount(token);
    }

    @PostMapping("/send-validation-email")
    public ResponseEntity<?> sendVValidationEmail (
            @RequestParam String email
    )throws MessagingException{
        service.resendActivationEmail(email);
        return ResponseEntity.accepted().build();
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestParam String refreshToken) {

        AuthenticationResponse response = service.refreshToken(refreshToken);

        return ResponseEntity.ok(response);
    }
}
