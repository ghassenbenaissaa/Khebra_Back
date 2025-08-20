package com.example.Khebra.controller;

import com.example.Khebra.dto.ForgotPasswordRequest;
import com.example.Khebra.dto.ResetPasswordRequest;
import com.example.Khebra.service.impl.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService service;


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request) throws MessagingException {
        service.initiatePasswordReset(request.getEmail());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {
        service.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate-reset-token")
    public void validateResetToken(
            @RequestParam String token) {
        if (!service.isValidToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }
    }

}
