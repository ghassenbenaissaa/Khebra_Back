package com.example.Khebra.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {

    private int id;
    private int participant1Id;
    private int participant2Id;
    private String participant1FullName;
    private String participant2FullName;
    private List<MessageDto> messages;
    private String participant1ImageUrl;
    private String participant2ImageUrl;
}
