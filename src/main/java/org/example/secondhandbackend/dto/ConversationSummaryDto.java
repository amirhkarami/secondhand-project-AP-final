package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConversationSummaryDto {
    private Long conversationId;
    private String productTitle;
    private String otherUsername;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long productId;
}