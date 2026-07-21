package ir.ac.aut.secondhand.frontend.dto;


import java.time.LocalDateTime;

public class SendMessageResponseDto {

    private Long conversationId;
    private Long messageId;
    private LocalDateTime sentAt;

    public SendMessageResponseDto() {
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}
