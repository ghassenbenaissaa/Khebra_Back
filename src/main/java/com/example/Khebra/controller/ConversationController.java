package com.example.Khebra.controller;

import com.example.Khebra.dto.ConversationDto;
import com.example.Khebra.entity.StatusDemande;
import com.example.Khebra.entity.User;
import com.example.Khebra.service.IConversationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@AllArgsConstructor
@RequestMapping("conversation")
public class ConversationController {
    private IConversationService conversationService;

    @GetMapping("/all")
    public Page<ConversationDto> getAllConversation(@AuthenticationPrincipal User user,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size) {
        return conversationService.GetAllConversation(user.getId(),page,size);
    }

    @GetMapping
    public ConversationDto GetConversation(
                                           @RequestParam int conversationId) {
        return conversationService.GetConversation(conversationId);
    }
  
    @PutMapping("/status")
    public ResponseEntity<?> updateDemandeStatus(@RequestParam int id) {
        conversationService.UpdateConversationStatus(id);
        return ResponseEntity.ok().build();
    }
}
