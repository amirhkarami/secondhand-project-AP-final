//added this file in commit num 3
package org.example.secondhandbackend.service;

import org.example.secondhandbackend.dto.ProductSummaryDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.*;
import org.example.secondhandbackend.repository.FavoriteRepository;
import org.example.secondhandbackend.repository.ProductRepository;
import org.example.secondhandbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void addFavorite(int productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("advertisement not found", 404));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ApiException("product is not active", 400);
        }

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new ApiException("product is already a favorite", 400);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .build();

        favoriteRepository.save(favorite);
    }

    public void removeFavorite(int productId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("advertisement not found", 404));

        Favorite favorite = favoriteRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ApiException("product is not a favorite", 400));

        favoriteRepository.delete(favorite);
    }

    public List<ProductSummaryDto> getFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));

        return favoriteRepository.findByUser(user).stream()
                .map(f -> toSummaryDto(f.getProduct()))
                .collect(Collectors.toList());
    }

    private ProductSummaryDto toSummaryDto(Product p) {
        String imagePath = null;

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            imagePath = p.getImages()
                    .get(0)
                    .getImagePath();
        }
        return new ProductSummaryDto(
                p.getId(),
                p.getTitle(),
                p.getPrice(),
                p.getCity().getName(),
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getStatus().name(),
                imagePath,
                p.getUser() != null ? p.getUser().getUsername() : null   // ← خط جدید
        );
    }
}