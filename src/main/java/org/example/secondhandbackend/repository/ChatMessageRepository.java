package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.ChatMessage;
import org.example.secondhandbackend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversationOrderBySentAtAsc(Conversation conversation);
    List<ChatMessage> findTop1ByConversationOrderBySentAtDesc(Conversation conversation);
}