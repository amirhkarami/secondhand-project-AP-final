package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.Conversation;
import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByProductAndBuyer(Product product, User buyer);
    List<Conversation> findByBuyerOrSeller(User buyer, User seller);
}