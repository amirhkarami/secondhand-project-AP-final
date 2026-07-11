
package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.dto.UserSummaryDto;
import org.example.secondhandbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllUsers(Authentication authentication) {
        return ResponseEntity.ok(userService.getAllUsers(authentication.getName()));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<String> block(@PathVariable int id, Authentication authentication) {
        userService.blockUser(id, authentication.getName());
        return ResponseEntity.ok("user blocked");
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<String> unblock(@PathVariable int id, Authentication authentication) {
        userService.unblockUser(id, authentication.getName());
        return ResponseEntity.ok("user unblocked");
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<String> promote(@PathVariable int id, Authentication authentication) {
        userService.promoteToAdmin(id, authentication.getName());
        return ResponseEntity.ok("user promoted to admin");
    }
}