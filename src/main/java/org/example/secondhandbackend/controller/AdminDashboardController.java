
package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.dto.DashboardDto;
import org.example.secondhandbackend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final ProductService productService;

    public AdminDashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard(Authentication authentication) {
        return ResponseEntity.ok(productService.getDashboard(authentication.getName()));
    }
}