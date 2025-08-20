package com.example.Khebra.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Notification {

    @GeneratedValue
    @Id
    private int Id;
    private String senderEmail;
    private String receiverEmail;
    private String message;
    private Date timestamp;
    @Enumerated(EnumType.STRING)
    private TypeNotification typeNotification;


}
