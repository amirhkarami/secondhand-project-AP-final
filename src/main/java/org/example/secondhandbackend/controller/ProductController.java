
package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.dto.ProductDetailDto;
import org.example.secondhandbackend.dto.ProductSummaryDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.example.secondhandbackend.exception.ApiException;
import java.util.Map;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createProduct(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam long price,
            @RequestParam int categoryId,
            @RequestParam int cityId,
            @RequestParam(required = false) List<MultipartFile> images,
            Authentication authentication) {
        if (authentication == null) {
            throw new ApiException("UN", 401);
        }
        String username = authentication.getName();
        productService.createProduct(title, description, price, categoryId, cityId, images, username);
        return ResponseEntity.ok("Advertisement created and its waiting for admin approval ");
    }

    @GetMapping
    public ResponseEntity<List<ProductSummaryDto>> getActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductSummaryDto>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(productService.searchProducts(keyword, categoryId, cityId, minPrice, maxPrice, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDto> getProductDetails(@PathVariable int id, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(productService.getProductDetails(id, username));
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> editProduct(
            @PathVariable int id,
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        if (authentication == null) throw new ApiException("UNAUTHORIZED", 401);

        String title = (String) body.get("title");
        String description = (String) body.get("description");
        Long price = body.get("price") != null ? Long.valueOf(body.get("price").toString()) : null;
        Integer categoryId = body.get("categoryId") != null ? Integer.valueOf(body.get("categoryId").toString()) : null;
        Integer cityId = body.get("cityId") != null ? Integer.valueOf(body.get("cityId").toString()) : null;

        productService.editProduct(id, title, description, price, categoryId, cityId, authentication.getName());
        return ResponseEntity.ok("advertisement edited");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id, Authentication authentication) {
        if (authentication == null) throw new ApiException("UNAUTHORIZED", 401);
        productService.deleteProduct(id, authentication.getName());
        return ResponseEntity.ok("advertisement deleted");
    }

    @PutMapping("/{id}/sold")
    public ResponseEntity<String> markAsSold(@PathVariable int id, Authentication authentication) {
        if (authentication == null) throw new ApiException("UNAUTHORIZED", 401);
        productService.markAsSold(id, authentication.getName());
        return ResponseEntity.ok("advertisement sold");
    }
}