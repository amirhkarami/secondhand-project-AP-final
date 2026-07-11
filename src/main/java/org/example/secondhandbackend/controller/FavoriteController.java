
package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.dto.ProductSummaryDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<String> addFavorite(@PathVariable int productId, Authentication authentication) {
        if (authentication == null) {
            throw new ApiException("UNAUTHORIZED", 401);
        }
        favoriteService.addFavorite(productId, authentication.getName());
        return ResponseEntity.ok("added to favorites");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFavorite(@PathVariable int productId, Authentication authentication) {
        if (authentication == null) {
            throw new ApiException("UNAUTHORIZED", 401);
        }
        favoriteService.removeFavorite(productId, authentication.getName());
        return ResponseEntity.ok("deleted from favorites");
    }

    @GetMapping
    public ResponseEntity<List<ProductSummaryDto>> getFavorites(Authentication authentication) {
        if (authentication == null) {
            throw new ApiException("UNAUTHORIZED", 401);
        }
        return ResponseEntity.ok(favoriteService.getFavorites(authentication.getName()));
    }
}