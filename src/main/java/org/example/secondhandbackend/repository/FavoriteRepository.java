
package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.Favorite;
import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndProduct(User user, Product product);
    boolean existsByUserAndProduct(User user, Product product);
    List<Favorite> findByUser(User user);
}