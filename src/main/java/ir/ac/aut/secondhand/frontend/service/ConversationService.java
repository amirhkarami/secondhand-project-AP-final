package ir.ac.aut.secondhand.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.dto.ChatMessageDto;
import ir.ac.aut.secondhand.frontend.dto.ConversationDto;
import ir.ac.aut.secondhand.frontend.dto.MessageReceiptDto;
import ir.ac.aut.secondhand.frontend.dto.SendMessageResponseDto;
import ir.ac.aut.secondhand.frontend.dto.request.MessageRequest;

import java.util.List;

public final class ConversationService {
    private final ApiClient apiClient;

    public ConversationService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public MessageReceiptDto sendMessage(long productId, String content) {
        return apiClient.postJson("/api/conversations/" + productId + "/messages",
                new MessageRequest(content), new TypeReference<MessageReceiptDto>() { }, true);
    }

    public List<ConversationDto> getConversations() {
        return apiClient.get("/api/conversations",
                new TypeReference<List<ConversationDto>>() { }, true);
    }

    public List<ChatMessageDto> getMessages(long conversationId) {
        return apiClient.get("/api/conversations/" + conversationId + "/messages",
                new TypeReference<List<ChatMessageDto>>() { }, true);
    }

    public SendMessageResponseDto sendReply(Long conversationId, String content) {

        MessageRequest request = new MessageRequest(content);

        return apiClient.postJson(
                "/api/conversations/" + conversationId + "/reply",
                request,
                new TypeReference<SendMessageResponseDto>() {},
                true
        );
    }
}
