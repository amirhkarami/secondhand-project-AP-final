package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.dto.*;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/conversations/{productId}/messages")
    public ResponseEntity<SendMessageResponseDto> sendMessage(
            @PathVariable int productId,
            @RequestBody SendMessageRequestDto body,
            Authentication authentication) {
        if (authentication == null) throw new ApiException("UNAUTHORIZED", 401);
        return ResponseEntity.ok(chatService.sendMessage(productId, body.getContent(), authentication.getName()));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationSummaryDto>> getConversations(Authentication authentication) {
        if (authentication == null) throw new ApiException("UNAUTHORIZED", 401);
        return ResponseEntity.ok(chatService.getUserConversations(authentication.getName()));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessages(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) throw new ApiException("UNAUTHORIZED", 401);
        return ResponseEntity.ok(chatService.getMessages(id, authentication.getName()));
    }
}