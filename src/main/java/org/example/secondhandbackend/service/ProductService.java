// ProductService.java - جایگزین کن
package org.example.secondhandbackend.service;

import org.example.secondhandbackend.dto.ProductDetailDto;
import org.example.secondhandbackend.dto.ProductImageDto;
import org.example.secondhandbackend.dto.ProductSummaryDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.*;
import org.example.secondhandbackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public ProductService(ProductRepository productRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          CityRepository cityRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
    }

    public Product createProduct(String title, String description, long price, int categoryId, int cityId, List<MultipartFile> images, String username) {
        if (title == null || title.isBlank()) {
            throw new ApiException("عنوان آگهی نمی‌تواند خالی باشد", 400);
        }
        if (price <= 0) {
            throw new ApiException("قیمت واردشده معتبر نیست", 400);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("کاربر پیدا نشد", 404));

        Category category = categoryRepository.findById((long) categoryId)
                .orElseThrow(() -> new ApiException("دسته‌بندی پیدا نشد", 404));

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ApiException("شهر پیدا نشد", 404));

        Product product = Product.builder()
                .title(title)
                .description(description)
                .price(price)
                .user(user)
                .category(category)
                .city(city)
                .status(ProductStatus.PENDING)
                .build();

        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : images) {
                try {
                    ProductImage image = ProductImage.builder()
                            .imageData(file.getBytes())
                            .product(product)
                            .build();
                    productImages.add(image);
                } catch (IOException e) {
                    throw new ApiException("خطا در خواندن فایل تصویر", 400);
                }
            }
            product.setImages(productImages);
        }

        return productRepository.save(product);
    }

    public List<ProductSummaryDto> getActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    public List<ProductSummaryDto> searchProducts(String keyword, Integer categoryId, Integer cityId, Long minPrice, Long maxPrice) {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .filter(p -> keyword == null || keyword.isBlank()
                        || p.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(p -> categoryId == null || (p.getCategory() != null && p.getCategory().getId().equals((long) categoryId.intValue())))
                .filter(p -> cityId == null || (p.getCity() != null && p.getCity().getId() == cityId))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    public ProductDetailDto getProductDetails(int id, String requesterUsername) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Advertisement not found", 404));

        boolean isOwner = requesterUsername != null && product.getUser().getUsername().equals(requesterUsername);

        boolean requesterIsAdmin = false;
        if (requesterUsername != null) {
            requesterIsAdmin = userRepository.findByUsername(requesterUsername)
                    .map(u -> u.getType() == UserType.ADMIN)
                    .orElse(false);
        }

        if (product.getStatus() != ProductStatus.ACTIVE && !isOwner && !requesterIsAdmin) {
            throw new ApiException("Advertisement not found", 404);
        }

        List<ProductImageDto> imageDtos = new ArrayList<>();
        if (product.getImages() != null) {
            for (ProductImage img : product.getImages()) {
                imageDtos.add(new ProductImageDto(img.getId(), Base64.getEncoder().encodeToString(img.getImageData())));
            }
        }

        return new ProductDetailDto(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getCity().getName(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getStatus().name(),
                product.getRejectReason(),
                product.getUser().getUsername(),
                product.getUser().getFullName(),
                isOwner,
                imageDtos
        );
    }

    public void approveProduct(int id, String adminUsername) {
        checkAdmin(adminUsername);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Advertisement not found", 404));
        product.setStatus(ProductStatus.ACTIVE);
        product.setRejectReason(null);
        productRepository.save(product);
    }

    public void rejectProduct(int id, String reason, String adminUsername) {
        checkAdmin(adminUsername);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Advertisement not found", 404));
        product.setStatus(ProductStatus.DENIED);
        product.setRejectReason(reason);
        productRepository.save(product);
    }

    public List<ProductSummaryDto> getPendingProducts(String adminUsername) {
        checkAdmin(adminUsername);
        return productRepository.findByStatus(ProductStatus.PENDING).stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    private void checkAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", 404));
        if (user.getType() != UserType.ADMIN) {
            throw new ApiException("user is not admin", 403);
        }
    }

    private ProductSummaryDto toSummaryDto(Product p) {
        return new ProductSummaryDto(
                p.getId(),
                p.getTitle(),
                p.getPrice(),
                p.getCity().getName(),
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getStatus().name()
        );
    }
}