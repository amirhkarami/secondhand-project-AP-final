
package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.model.Category;
import org.example.secondhandbackend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Long superCategoryId = null;

        Object superCategoryObj = body.get("superCategory");
        if (superCategoryObj instanceof Map) {
            Object idObj = ((Map<?, ?>) superCategoryObj).get("id");
            if (idObj != null) {
                superCategoryId = Long.valueOf(idObj.toString());
            }
        }

        Category savedCategory = categoryService.create(name, superCategoryId);
        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return new ResponseEntity<>("category deleted", HttpStatus.OK);
    }
}