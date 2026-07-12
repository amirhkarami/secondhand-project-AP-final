// dto/SendMessageResponseDto.java
package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SendMessageResponseDto {
    private Long conversationId;
    private Long messageId;
    private LocalDateTime sentAt;
}