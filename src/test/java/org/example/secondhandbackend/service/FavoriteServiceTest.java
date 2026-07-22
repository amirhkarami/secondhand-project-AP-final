package org.example.secondhandbackend.service;

import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.Favorite;
import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.model.ProductStatus;
import org.example.secondhandbackend.model.User;
import org.example.secondhandbackend.repository.FavoriteRepository;
import org.example.secondhandbackend.repository.ProductRepository;
import org.example.secondhandbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    private FavoriteService favoriteService;

    private User user;
    private Product activeProduct;
    private Product pendingProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        favoriteService = new FavoriteService(favoriteRepository, productRepository, userRepository);

        user = User.builder().id(1).username("ali").fullName("Ali").phoneNumber("09120000000")
                .password("x").isActive(true).build();
        activeProduct = Product.builder().id(5).title("Phone").price(500).status(ProductStatus.ACTIVE).build();
        pendingProduct = Product.builder().id(6).title("Watch").price(200).status(ProductStatus.PENDING).build();
    }

    @Test
    void addFavorite_savesWhenNotAlreadyFavorited() {
        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(productRepository.findById(5)).thenReturn(Optional.of(activeProduct));
        when(favoriteRepository.existsByUserAndProduct(user, activeProduct)).thenReturn(false);

        assertDoesNotThrow(() -> favoriteService.addFavorite(5, "ali"));
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    void addFavorite_throwsOnDuplicateFavorite() {
        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(productRepository.findById(5)).thenReturn(Optional.of(activeProduct));
        when(favoriteRepository.existsByUserAndProduct(user, activeProduct)).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class,
                () -> favoriteService.addFavorite(5, "ali"));

        assertEquals(400, ex.getStatus());
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_throwsWhenProductIsNotActive() {
        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(productRepository.findById(6)).thenReturn(Optional.of(pendingProduct));

        ApiException ex = assertThrows(ApiException.class,
                () -> favoriteService.addFavorite(6, "ali"));

        assertEquals(400, ex.getStatus());
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void removeFavorite_throwsWhenFavoriteDoesNotExist() {
        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(productRepository.findById(5)).thenReturn(Optional.of(activeProduct));
        when(favoriteRepository.findByUserAndProduct(user, activeProduct)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> favoriteService.removeFavorite(5, "ali"));

        assertEquals(400, ex.getStatus());
        verify(favoriteRepository, never()).delete(any());
    }

    @Test
    void removeFavorite_deletesWhenFavoriteExists() {
        Favorite favorite = Favorite.builder().user(user).product(activeProduct).build();
        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(productRepository.findById(5)).thenReturn(Optional.of(activeProduct));
        when(favoriteRepository.findByUserAndProduct(user, activeProduct)).thenReturn(Optional.of(favorite));

        assertDoesNotThrow(() -> favoriteService.removeFavorite(5, "ali"));
        verify(favoriteRepository, times(1)).delete(favorite);
    }
}