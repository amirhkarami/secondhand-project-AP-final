package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String senderUsername;
    private String content;
    private LocalDateTime sentAt;
    private boolean seen;
}