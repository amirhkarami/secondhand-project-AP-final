
package org.example.secondhandbackend.service;

import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.Category;
import org.example.secondhandbackend.repository.CategoryRepository;
import org.example.secondhandbackend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category create(String name, Long superCategoryId) {
        if (name == null || name.isBlank()) {
            throw new ApiException("category name can not be empty", 400);
        }
        if (categoryRepository.findByName(name).isPresent()) {
            throw new ApiException("Category name already used", 400);
        }

        Category superCategory = null;
        if (superCategoryId != null) {
            superCategory = categoryRepository.findById(superCategoryId)
                    .orElseThrow(() -> new ApiException("superCategory_id is invalid", 400));
        }

        Category category = new Category(name, superCategory);
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException("category not found", 404));

        boolean isSuperOfOthers = categoryRepository.findAll().stream()
                .anyMatch(c -> c.getSuperCategory() != null && c.getSuperCategory().getId().equals(id));

        if (isSuperOfOthers) {
            throw new ApiException("this category is the superCategory of other Categories and cannot be deleted", 400);
        }

        boolean usedByProducts = productRepository.findAll().stream()
                .anyMatch(p -> p.getCategory() != null && Objects.equals(p.getCategory().getId(), id));
        if (usedByProducts) {
            throw new ApiException("this category is used by existing advertisements and cannot be deleted", 400);
        }

        categoryRepository.deleteById(id);
    }
}