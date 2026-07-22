package org.example.secondhandbackend.service;

import org.example.secondhandbackend.dto.ProductSummaryDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.Category;
import org.example.secondhandbackend.model.City;
import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.model.ProductStatus;
import org.example.secondhandbackend.repository.CategoryRepository;
import org.example.secondhandbackend.repository.CityRepository;
import org.example.secondhandbackend.repository.ProductRepository;
import org.example.secondhandbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
class ProductServiceSearchTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private CloudinaryService cloudinaryService;

    private ProductService productService;

    private Category electronics;
    private Category mobilePhones;
    private City tehran;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository, userRepository, categoryRepository,
                cityRepository, cloudinaryService);

        electronics = new Category();
        electronics.setId(1L);
        electronics.setName("Electronics");

        mobilePhones = new Category();
        mobilePhones.setId(2L);
        mobilePhones.setName("Mobile phones");
        mobilePhones.setSuperCategory(electronics);

        tehran = new City("Tehran");
    }

    @Test
    void searchProducts_parentCategoryFilterIncludesSubcategoryProducts() {
        Product subcategoryProduct = Product.builder()
                .id(1).title("iPhone").price(100).status(ProductStatus.ACTIVE)
                .category(mobilePhones).city(tehran).build();

        when(productRepository.findByStatus(ProductStatus.ACTIVE)).thenReturn(List.of(subcategoryProduct));

        List<ProductSummaryDto> result = productService.searchProducts(null, 1, null, null, null, null);

        assertEquals(1, result.size(),
                "filtering by the parent category id should also return products from its subcategories");
        assertEquals("iPhone", result.get(0).getTitle());
    }

    @Test
    void searchProducts_categoryFilterExcludesUnrelatedCategory() {
        Category clothing = new Category();
        clothing.setId(3L);
        clothing.setName("Clothing");

        Product unrelatedProduct = Product.builder()
                .id(2).title("T-Shirt").price(50).status(ProductStatus.ACTIVE)
                .category(clothing).city(tehran).build();

        when(productRepository.findByStatus(ProductStatus.ACTIVE)).thenReturn(List.of(unrelatedProduct));

        List<ProductSummaryDto> result = productService.searchProducts(null, 1, null, null, null, null);

        assertTrue(result.isEmpty(), "a product from an unrelated category must not be returned");
    }

    @Test
    void searchProducts_rejectsMinPriceGreaterThanMaxPrice() {
        ApiException ex = assertThrows(ApiException.class,
                () -> productService.searchProducts(null, null, null, 1000L, 100L, null));

        assertEquals(400, ex.getStatus());
    }

    @Test
    void searchProducts_sortsByPriceAscending() {
        Product cheap = Product.builder().id(1).title("Cheap").price(100).status(ProductStatus.ACTIVE)
                .category(electronics).city(tehran).build();
        Product expensive = Product.builder().id(2).title("Expensive").price(900).status(ProductStatus.ACTIVE)
                .category(electronics).city(tehran).build();

        when(productRepository.findByStatus(ProductStatus.ACTIVE)).thenReturn(List.of(expensive, cheap));

        List<ProductSummaryDto> result = productService.searchProducts(null, null, null, null, null, "price_asc");

        assertEquals("Cheap", result.get(0).getTitle());
        assertEquals("Expensive", result.get(1).getTitle());
    }
}