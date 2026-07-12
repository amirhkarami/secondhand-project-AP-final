package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.model.Rating;
import org.example.secondhandbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByProductAndBuyer(Product product, User buyer);
    List<Rating> findBySeller(User seller);
}