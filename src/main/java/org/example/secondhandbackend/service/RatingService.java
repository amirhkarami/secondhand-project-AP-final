package org.example.secondhandbackend.service;

import org.example.secondhandbackend.dto.RatingItemDto;
import org.example.secondhandbackend.dto.SellerRatingsDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.model.Rating;
import org.example.secondhandbackend.model.User;
import org.example.secondhandbackend.repository.ProductRepository;
import org.example.secondhandbackend.repository.RatingRepository;
import org.example.secondhandbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void addRating(int productId, int score, String comment, String username) {
        if (score < 1 || score > 5) {
            throw new ApiException("score should be between 1 to 5", 400);
        }

        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("advertisement not found", 404));

        if (product.getUser().getUsername().equals(username)) {
            throw new ApiException("you can not score yourself", 400);
        }
        if (ratingRepository.findByProductAndBuyer(product, buyer).isPresent()) {
            throw new ApiException("you have already scored this product", 400);
        }

        Rating rating = Rating.builder()
                .product(product).buyer(buyer).seller(product.getUser())
                .score(score).comment(comment).build();

        ratingRepository.save(rating);
    }

    public SellerRatingsDto getSellerRatings(int userId) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("user not found", 404));

        List<Rating> ratings = ratingRepository.findBySeller(seller);
        double avg = ratings.stream().mapToInt(Rating::getScore).average().orElse(0);

        List<RatingItemDto> items = ratings.stream()
                .map(r -> new RatingItemDto(r.getScore(), r.getComment(), r.getBuyer().getUsername()))
                .collect(Collectors.toList());

        return new SellerRatingsDto(avg, ratings.size(), items);
    }
}