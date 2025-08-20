package com.example.Khebra.service;

import com.example.Khebra.dto.MessageDto;

public interface IMessageService {

    MessageDto getLatestMessage(int conversationId);
    MessageDto sendMessage( MessageDto message);
}
