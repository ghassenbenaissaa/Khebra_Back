package com.example.Khebra.service.impl;

import com.example.Khebra.dto.AuthenticationRequest;
import com.example.Khebra.dto.AuthenticationResponse;
import com.example.Khebra.dto.RegistrationRequest;
import com.example.Khebra.dto.Token;
import com.example.Khebra.email.EmailTemplateName;
import com.example.Khebra.entity.*;
import com.example.Khebra.exception.BusinessException;
import com.example.Khebra.handler.BusinessErrorCodes;
import com.example.Khebra.repository.DomaineRepository;
import com.example.Khebra.repository.TokenRepository;
import com.example.Khebra.repository.UserRepository;
import com.example.Khebra.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final DomaineRepository domaineRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;



    public void register(RegistrationRequest request) throws MessagingException {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new BusinessException(BusinessErrorCodes.EMAIL_ALREADY_USED);
        }else if (userRepository.existsByCin(request.getCin())){
            throw new BusinessException(BusinessErrorCodes.CIN_ALREADY_USED);
        }
        User user;
        if ("Expert".equalsIgnoreCase(request.getUserType())) {
            Domaine domaine = domaineRepository.findById(request.getDomaineId())
                    .orElseThrow(() -> new RuntimeException("Domaine non trouvé"));
            user = Expert.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .numTel(request.getNumTel())
                    .cin(request.getCin())
                    .IsActive(false)
                    .adresse(request.getAddress())
                    .expertise(request.getExpertise())
                    .domaine(domaine)
                    .biographie(request.getBiographie())
                    .point(request.getPoint())
                    .build();
        } else  if("Client".equalsIgnoreCase(request.getUserType())){
            user = Client.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .adresse(request.getAddress())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .numTel(request.getNumTel())
                    .cin(request.getCin())
                    .IsActive(false)
                    .interet(request.getInteret())
                    .build();
        }else {
            throw new IllegalArgumentException("Invalid userType: " + request.getUserType());
        }

        Image defaultImage = new Image();
        defaultImage.setImageUrl("https://res.cloudinary.com/db0stnc2a/image/upload/v1753974465/uploads/txieko7mffavdmkqbfjb.jpg");
        defaultImage.setUser(user);

        user.setImage(defaultImage);
        userRepository.save(user);
        sendValidationemail(user);
    }

    private void sendValidationemail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl + newToken,
                newToken,
                "Account Activation"

        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }



    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws MessagingException {
        // First, fetch the user from DB to validate existence
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.BAD_CREDENTIALS));

        // Now check activation status AFTER correct password
        if (!user.getIsActive()) {
            resendActivationEmail(user.getEmail());
            throw new BusinessException(BusinessErrorCodes.ACCOUNT_NOT_ACTIVATED);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            // Wrap it into your own custom exception
            throw new BusinessException(BusinessErrorCodes.INCORRECT_CURRENT_PASSWORD);
        }

        // Check expert validation
        if (user instanceof Expert expert && !expert.isValidated()) {
            throw new BusinessException(BusinessErrorCodes.ACCOUNT_LOCKED);
        }

        // Build JWT claims
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getFullName());

        String jwtToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(claims, user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId().toString())
                .isActive(user.getIsActive())
                .isBanned(user.isBanned())
                .role(user.getAuthorities().toString())
                .isValidated(user instanceof Expert ? ((Expert) user).isValidated() : null)
                .refreshToken(refreshToken)
                .build();
    }
    public AuthenticationResponse refreshToken(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new BusinessException(BusinessErrorCodes.REFRESH_TOKEN_EXPIRED);
        }
        Claims claims;
        try {
            claims = jwtService.extractAllClaims(refreshToken);
        } catch (ExpiredJwtException ex) {
            claims = ex.getClaims();  // still get claims even if expired
        }
        String email = claims.getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.USER_NOT_EXIST));


        claims.put("fullName", user.getFullName());

        String newAccessToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken) // or generate new refresh token here if you want rotation
                .userId(user.getId().toString())
                .isActive(user.getIsActive())
                .isBanned(user.isBanned())
                .role(user.getAuthorities().toString())
                .isValidated(user instanceof Expert ? ((Expert) user).isValidated() : null)
                .build();
    }


    @Transactional
    public void activateAccount(String token) throws MessagingException {
        // Validate token is not null or empty
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCodes.CODE_NOT_FOUND);
        }

        // Find the token in database
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CODE_NOT_FOUND));

        // Check if token is expired
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationemail(savedToken.getUser());
            throw new BusinessException(BusinessErrorCodes.CODE_EXPIRED);
        }

        // Check if token is already validated (optional additional check)
        if (savedToken.getValidatedAt() != null) {
            throw new BusinessException(BusinessErrorCodes.CODE_USED);
        }

        // Find and activate the user
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.USER_NOT_EXIST));

        user.setIsActive(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


    public void resendActivationEmail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.USER_NOT_EXIST));

        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalStateException("Account is already active");
        }

        sendValidationemail(user);
    }

}
