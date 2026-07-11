
package org.example.secondhandbackend.service;

import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.Category;
import org.example.secondhandbackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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

        categoryRepository.deleteById(id);
    }
}