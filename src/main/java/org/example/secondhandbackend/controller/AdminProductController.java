


// controller/AdminProductController.java
package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.dto.ProductSummaryDto;
import org.example.secondhandbackend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.Map;
//all of methods which are included in this file are only availabe as admin
@CrossOrigin
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductService productService;
    //DI
    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ProductSummaryDto>> getPending(Authentication authentication) {
        return ResponseEntity.ok(productService.getPendingProducts(authentication.getName()));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable int id, Authentication authentication) {
        productService.approveProduct(id, authentication.getName());
        return ResponseEntity.ok("advertisement accepted");
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable int id, @RequestBody Map<String, String> body, Authentication authentication) {
        productService.rejectProduct(id, body.get("reason"), authentication.getName());
        return ResponseEntity.ok("advertisement declined");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteByAdmin(@PathVariable int id, Authentication authentication) {
        productService.deleteByAdmin(id);
        return ResponseEntity.ok("advertisement deleted");
    }
}