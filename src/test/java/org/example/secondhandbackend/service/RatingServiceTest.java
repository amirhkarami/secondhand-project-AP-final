package org.example.secondhandbackend.service;

import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.model.ProductStatus;
import org.example.secondhandbackend.model.Rating;
import org.example.secondhandbackend.model.User;
import org.example.secondhandbackend.repository.ProductRepository;
import org.example.secondhandbackend.repository.RatingRepository;
import org.example.secondhandbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    private RatingService ratingService;

    private User buyer;
    private User seller;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ratingService = new RatingService(ratingRepository, productRepository, userRepository);

        buyer = User.builder().id(1).username("buyer").fullName("Buyer").phoneNumber("09120000000")
                .password("x").isActive(true).build();
        seller = User.builder().id(2).username("seller").fullName("Seller").phoneNumber("09120000001")
                .password("x").isActive(true).build();
        product = Product.builder().id(10).title("Laptop").price(1000).status(ProductStatus.ACTIVE)
                .user(seller).build();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 6, 100})
    void addRating_rejectsScoreOutsideOneToFive(int invalidScore) {
        ApiException ex = assertThrows(ApiException.class,
                () -> ratingService.addRating(10, invalidScore, "nice", "buyer"));

        assertEquals(400, ex.getStatus());
        verifyNoInteractions(ratingRepository, productRepository, userRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void addRating_acceptsScoresInValidRange(int validScore) {
        when(userRepository.findByUsername("buyer")).thenReturn(Optional.of(buyer));
        when(productRepository.findById(10)).thenReturn(Optional.of(product));
        when(ratingRepository.findByProductAndBuyer(product, buyer)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> ratingService.addRating(10, validScore, "ok", "buyer"));
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void addRating_throwsWhenBuyerRatesOwnProduct() {
        when(userRepository.findByUsername("seller")).thenReturn(Optional.of(seller));
        when(productRepository.findById(10)).thenReturn(Optional.of(product));

        ApiException ex = assertThrows(ApiException.class,
                () -> ratingService.addRating(10, 5, "self rating", "seller"));

        assertEquals(400, ex.getStatus());
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void addRating_throwsOnDuplicateRatingForSameProduct() {
        when(userRepository.findByUsername("buyer")).thenReturn(Optional.of(buyer));
        when(productRepository.findById(10)).thenReturn(Optional.of(product));
        when(ratingRepository.findByProductAndBuyer(product, buyer))
                .thenReturn(Optional.of(Rating.builder().id(1L).build()));

        ApiException ex = assertThrows(ApiException.class,
                () -> ratingService.addRating(10, 4, "again", "buyer"));

        assertEquals(400, ex.getStatus());
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void addRating_throwsWhenProductDoesNotExist() {
        when(userRepository.findByUsername("buyer")).thenReturn(Optional.of(buyer));
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> ratingService.addRating(999, 5, "x", "buyer"));

        assertEquals(404, ex.getStatus());
    }
}