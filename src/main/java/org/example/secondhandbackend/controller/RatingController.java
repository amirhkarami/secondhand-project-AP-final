package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.dto.RatingRequestDto;
import org.example.secondhandbackend.dto.SellerRatingsDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<String> addRating(@RequestBody RatingRequestDto body, Authentication authentication) {
        if (authentication == null) throw new ApiException("UNAUTHORIZED", 401);
        ratingService.addRating(body.getProductId(), body.getScore(), body.getComment(), authentication.getName());
        return ResponseEntity.ok("rating has been recorded");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SellerRatingsDto> getSellerRatings(@PathVariable int userId) {
        return ResponseEntity.ok(ratingService.getSellerRatings(userId));
    }
}