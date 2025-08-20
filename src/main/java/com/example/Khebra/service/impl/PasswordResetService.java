package com.example.Khebra.service.impl;

import com.example.Khebra.dto.PasswordResetToken;
import com.example.Khebra.email.EmailTemplateName;
import com.example.Khebra.exception.BusinessException;
import com.example.Khebra.handler.BusinessErrorCodes;
import com.example.Khebra.repository.PasswordResetTokenRepository;
import com.example.Khebra.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    @Value("${application.mailing.frontend.password-reset-url}")
    private String frontendResetUrl;

    @Transactional
    public void initiatePasswordReset(String email) throws MessagingException {
       userRepository.findByEmail(email)
               .orElseThrow(() -> new BusinessException(BusinessErrorCodes.USER_NOT_EXIST));

        // Delete any existing tokens for this email
        tokenRepository.deleteByEmail(email);

        // Generate new token
        String token = UUID.randomUUID().toString();
        String resetUrl = frontendResetUrl + token;
        // Save token (valid for 15 minutes)
        var resetToken = PasswordResetToken.builder()
                .token(token)
                .email(email)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        sendResetEmail(email, resetUrl);
        tokenRepository.save(resetToken);

    }


    @Transactional
    public void resetPassword(String token, String newPassword) {
        var resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token has expired");
        }

        if (resetToken.getUsed()) {
            throw new RuntimeException("Token has already been used");
        }

        // Update user password
        var user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    public boolean isValidToken(String token) {
        return tokenRepository.findByToken(token)
                .map(resetToken -> !resetToken.isExpired() && !resetToken.getUsed())
                .orElse(false);
    }

    private void sendResetEmail(String email, String resetUrl) throws MessagingException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.USER_NOT_EXIST));

        emailService.sendEmail(
                email,
                user.getFullName(),
                EmailTemplateName.RESET_PASSWORD,
                resetUrl,
                null,
                "Reset Your Password"
        );
    }

}
