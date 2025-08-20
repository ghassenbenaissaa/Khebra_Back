package com.example.Khebra.controller;

import com.example.Khebra.dto.ClientDto;
import com.example.Khebra.dto.ExpertDto;
import com.example.Khebra.security.JwtService;
import com.example.Khebra.service.IClientService;
import com.example.Khebra.service.IExpertService;
import com.example.Khebra.service.impl.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final JwtService jwtService;
    private final IClientService clientService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final IExpertService expertService;

    @PutMapping("/profile")
    public ResponseEntity<Object> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> payload
    ) {
        String token = jwtService.extractToken(authHeader);
        String email = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        if ("ROLE_CLIENT".equals(role)) {
            ClientDto clientDto = objectMapper.convertValue(payload, ClientDto.class);
            clientDto.setEmail(email);
            ClientDto updatedClient = clientService.updateClient(clientDto);
            return ResponseEntity.ok(updatedClient);

        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Invalid user role: " + role);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(@RequestHeader("Authorization") String authHeader) {

        String token = jwtService.extractToken(authHeader);
        String email = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        if ("ROLE_CLIENT".equals(role)) {
            ClientDto dto = clientService.getClientByEmail(email);
            dto.setRole(role);
            return ResponseEntity.ok(dto);
        } if ("ROLE_EXPERT".equals(role)) {
            ExpertDto dto = expertService.getExpertByEmail(email);
            dto.setRole(role);
            return ResponseEntity.ok(dto);}
        if ("ROLE_ADMIN".equals(role)) {
            ClientDto dto = clientService.getClientByEmail(email);
            dto.setRole(role);
            return ResponseEntity.ok(dto);}

        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user role: " + role);
        }
    }


    @PostMapping("/ban/{userId}")
    public ResponseEntity<?> BanUser(@PathVariable int userId) {
        userService.BanUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Expert banni avec succès");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unban/{userId}")
    public ResponseEntity<?> UnBanUser(@PathVariable int userId) {
        userService.UnBanUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Expert débanni avec succès");
        return ResponseEntity.ok(response);
    }



}
