package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessageDto {
    private Long id;
    private String senderUsername;
    private String content;
    private LocalDateTime sentAt;
    private boolean seen;

    public ChatMessageDto() {
    }

    public ChatMessageDto(Long id, String senderUsername, String content,
                          LocalDateTime sentAt, boolean seen) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.content = content;
        this.sentAt = sentAt;
        this.seen = seen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
