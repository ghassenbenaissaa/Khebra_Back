package com.example.Khebra.controller;

import com.example.Khebra.dto.ConversationDto;
import com.example.Khebra.dto.MessageDto;
import com.example.Khebra.service.IMessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("message")
@AllArgsConstructor
public class MessageController {

    private final IMessageService messageService;
    @PostMapping
    public MessageDto SendMessage(@RequestBody MessageDto messageDto) {
        return messageService.sendMessage(messageDto);
    }
}
