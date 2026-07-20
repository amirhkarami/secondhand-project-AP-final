
package org.example.secondhandbackend.service;

import org.example.secondhandbackend.dto.DashboardDto;
import org.example.secondhandbackend.dto.ProductDetailDto;
import org.example.secondhandbackend.dto.ProductImageDto;
import org.example.secondhandbackend.dto.ProductSummaryDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.*;
import org.example.secondhandbackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final CloudinaryService cloudinaryService;

    public ProductService(ProductRepository productRepository, UserRepository userRepository, CategoryRepository categoryRepository, CityRepository cityRepository, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.cloudinaryService = cloudinaryService;
    }

    private boolean categoryMatches(Category category, Long targetCategoryId) {
        Category current = category;
        while (current != null) {
            if (current.getId().equals(targetCategoryId)) {
                return true;
            }
            current = current.getSuperCategory();
        }
        return false;
    }

    public Product createProduct(String title, String description, long price, int categoryId, int cityId, List<MultipartFile> images, String username) {
        if (title == null || title.isBlank()) {
            throw new ApiException("advertisement title cannot be empty", 400);
        }
        if (price <= 0) {
            throw new ApiException("price is invalid", 400);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", 404));

        Category category = categoryRepository.findById((long) categoryId)
                .orElseThrow(() -> new ApiException("Category not found", 404));

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ApiException("City not found", 404));

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
                    String imageUrl = cloudinaryService.upload(file);
                    ProductImage image = ProductImage.builder()
                            .imagePath(imageUrl)
                            .product(product)
                            .build();
                    productImages.add(image);
                } catch (Exception e) {
                    throw new ApiException("unable to process image", 400);
                }
            }
            product.setImages(productImages);
        }
        // added in commit num 3.I have forgotten that every advertisement cant have more than 5 images
        if (images != null && images.size() > 5) {
            throw new ApiException("Each advertiesment cant have more that 5 images", 400);
        }

        return productRepository.save(product);
    }

    public List<ProductSummaryDto> getActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }
    /*
         I have fixed an issue in commit num 3 which minPrice can be higher than maxPrice
        and also added the ability to sort */
    public List<ProductSummaryDto> searchProducts(String keyword, Integer categoryId, Integer cityId,
                                                  Long minPrice, Long maxPrice, String sortBy) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new ApiException("minPrice cannot be more than maxPrice", 400);
        }

        List<Product> result = productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .filter(p -> keyword == null || keyword.isBlank()
                        || p.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(p -> categoryId == null || categoryMatches(p.getCategory(), (long) categoryId.intValue()))
                .filter(p -> cityId == null || (p.getCity() != null && p.getCity().getId() == cityId))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .collect(java.util.stream.Collectors.toList());

        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc" -> result.sort((a, b) -> Long.compare(a.getPrice(), b.getPrice()));
                case "price_desc" -> result.sort((a, b) -> Long.compare(b.getPrice(), a.getPrice()));
                case "newest" -> result.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
            }
        }

        return result.stream().map(this::toSummaryDto).collect(java.util.stream.Collectors.toList());
    }

    public ProductDetailDto getProductDetails(int id, String requesterUsername) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ApiException("Advertisement not found", 404));
        //String imagePath = product.getImages().getFirst().getImagePath();
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
                System.out.println(
                        "DETAIL IMAGE = " + img.getImagePath()
                );
                imageDtos.add(new ProductImageDto(img.getId(), img.getImagePath()));
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
                product.getUser().getId(),
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

    //    private ProductSummaryDto toSummaryDto(Product p) {
//        return new ProductSummaryDto(
//                p.getId(),
//                p.getTitle(),
//                p.getPrice(),
//                p.getCity().getName(),
//                p.getCategory() != null ? p.getCategory().getName() : null,
//                p.getStatus().name()
//        );
//    }
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
                p.getUser() != null ? p.getUser().getUsername() : null
        );
    }




    public void editProduct(int id, String title, String description, Long price, Integer categoryId, Integer cityId, List<MultipartFile> images, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("advertisement not found", 404));

        User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));

        boolean isOwner = product.getUser().getUsername().equals(username);
        boolean isAdmin = requester.getType() == UserType.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ApiException("you do not own this advertisement", 403);
        }

        if (product.getStatus() == ProductStatus.SOLD || product.getStatus() == ProductStatus.DELETED) {
            throw new ApiException("advertisement is sold or deleted", 400);
        }

        if (title != null && !title.isBlank()) {
            product.setTitle(title);
        }
        if (description != null) {
            product.setDescription(description);
        }
        if (price != null) {
            if (price <= 0) {
                throw new ApiException("price should be higher than 0", 400);
            }
            product.setPrice(price);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById((long) categoryId)
                    .orElseThrow(() -> new ApiException("category not found", 404));
            product.setCategory(category);
        }
        if (cityId != null) {
            City city = cityRepository.findById(cityId)
                    .orElseThrow(() -> new ApiException("city not found", 404));
            product.setCity(city);
        }

        product.setStatus(ProductStatus.PENDING);
        product.setRejectReason(null);

        productRepository.save(product);
        // this is for edit
        if (images != null && !images.isEmpty()) {

            List<ProductImage> productImages = new ArrayList<>();

            for (MultipartFile file : images) {

                try {

                    String imageUrl = cloudinaryService.upload(file);

                    ProductImage image = ProductImage.builder()
                            .imagePath(imageUrl)
                            .product(product)
                            .build();

                    productImages.add(image);

                } catch (Exception e) {
                    throw new ApiException("unable to process image",400);
                }
            }

            product.setImages(productImages);
        }
    }

    public void deleteProduct(int id, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("advertisement not found", 404));

        if (!product.getUser().getUsername().equals(username)) {
            throw new ApiException("you are not the owner of this advertisement", 403);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            throw new ApiException("advertisement already deleted", 400);
        }

        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }

    public void markAsSold(int id, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("advertisement not found", 404));

        if (!product.getUser().getUsername().equals(username)) {
            throw new ApiException("you are not the owner of this advertisement", 403);
        }

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ApiException("advertisement is not ACTIVE", 400);
        }

        product.setStatus(ProductStatus.SOLD);
        productRepository.save(product);
    }

    public void deleteByAdmin(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("advertisement not found", 404));
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }

    public DashboardDto getDashboard(String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ApiException("user not found", 404));
        if (admin.getType() != UserType.ADMIN) {
            throw new ApiException("you are not admin", 403);
        }

        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long pendingProducts = productRepository.findByStatus(ProductStatus.PENDING).size();
        long activeProducts = productRepository.findByStatus(ProductStatus.ACTIVE).size();
        long blockedUsers = userRepository.findAll().stream().filter(u -> !u.isActive()).count();

        return new DashboardDto(totalUsers, totalProducts, pendingProducts, activeProducts, blockedUsers);
    }
}