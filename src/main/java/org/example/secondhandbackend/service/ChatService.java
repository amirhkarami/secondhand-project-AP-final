package org.example.secondhandbackend.service;

import org.example.secondhandbackend.dto.ChatMessageDto;
import org.example.secondhandbackend.dto.ConversationSummaryDto;
import org.example.secondhandbackend.dto.SendMessageResponseDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.*;
import org.example.secondhandbackend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ChatService(ConversationRepository conversationRepository,
                       ChatMessageRepository chatMessageRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public SendMessageResponseDto sendMessage(int productId, String content, String username) {
        if (content == null || content.isBlank()) {
            throw new ApiException("content cannot be empty", 400);
        }

        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("advertisement not found", 404));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ApiException("advertisement is not active", 400);
        }
        if (product.getUser().getUsername().equals(username)) {
            throw new ApiException("you cannot message yourself", 400);
        }

        User seller = product.getUser();

        Conversation conversation = conversationRepository.findByProductAndBuyer(product, buyer)
                .orElseGet(() -> conversationRepository.save(
                        Conversation.builder().product(product).buyer(buyer).seller(seller).build()));

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation).sender(buyer).content(content)
                .sentAt(LocalDateTime.now()).seen(false).build();

        chatMessageRepository.save(message);

        return new SendMessageResponseDto(conversation.getId(), message.getId(), message.getSentAt());
    }

    public List<ConversationSummaryDto> getUserConversations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));

        List<Conversation> conversations = conversationRepository.findByBuyerOrSeller(user, user);

        return conversations.stream().map(c -> {
            User other = c.getBuyer().getUsername().equals(username) ? c.getSeller() : c.getBuyer();
            List<ChatMessage> lastMsgList = chatMessageRepository.findTop1ByConversationOrderBySentAtDesc(c);
            String lastMessage = lastMsgList.isEmpty() ? null : lastMsgList.get(0).getContent();
            LocalDateTime lastTime = lastMsgList.isEmpty() ? null : lastMsgList.get(0).getSentAt();
            return new ConversationSummaryDto(c.getId(), c.getProduct().getTitle(), other.getUsername(), lastMessage, lastTime ,(long) c.getProduct().getId());
        }).collect(Collectors.toList());
    }

    public List<ChatMessageDto> getMessages(Long conversationId, String username) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ApiException("conversation not found", 404));

        boolean isParticipant = conversation.getBuyer().getUsername().equals(username)
                || conversation.getSeller().getUsername().equals(username);
        if (!isParticipant) {
            throw new ApiException("you are not a part of this conversation", 403);
        }

        List<ChatMessage> messages = chatMessageRepository.findByConversationOrderBySentAtAsc(conversation);

        for (ChatMessage m : messages) {
            if (!m.getSender().getUsername().equals(username) && !m.isSeen()) {
                m.setSeen(true);
                chatMessageRepository.save(m);
            }
        }

        return messages.stream()
                .map(m -> new ChatMessageDto(m.getId(), m.getSender().getUsername(), m.getContent(), m.getSentAt(), m.isSeen()))
                .collect(Collectors.toList());
    }


    public SendMessageResponseDto replyMessage(
            Long conversationId,
            String content,
            String username
    ) {

        if (content == null || content.isBlank()) {
            throw new ApiException("content cannot be empty", 400);
        }


        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));


        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ApiException("conversation not found", 404));


        boolean isParticipant =
                conversation.getBuyer().getUsername().equals(username)
                        ||
                        conversation.getSeller().getUsername().equals(username);


        if (!isParticipant) {
            throw new ApiException("you are not part of this conversation", 403);
        }


        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .sentAt(LocalDateTime.now())
                .seen(false)
                .build();


        chatMessageRepository.save(message);


        return new SendMessageResponseDto(
                conversation.getId(),
                message.getId(),
                message.getSentAt()
        );
    }
}