package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageReceiptDto {
    private Long conversationId;
    private Long messageId;
    private LocalDateTime sentAt;

    public MessageReceiptDto() {
    }

    public MessageReceiptDto(Long conversationId, Long messageId, LocalDateTime sentAt) {
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.sentAt = sentAt;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
