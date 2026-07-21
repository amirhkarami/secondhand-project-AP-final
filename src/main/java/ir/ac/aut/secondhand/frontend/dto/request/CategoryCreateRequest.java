package ir.ac.aut.secondhand.frontend.dto.request;

import ir.ac.aut.secondhand.frontend.dto.CategoryDto;

public class CategoryCreateRequest {
    private Long id;
    private String name;
    private CategoryDto superCategory;

    public CategoryCreateRequest() {
    }

    public CategoryCreateRequest(Long id, String name, CategoryDto superCategory) {
        this.id = id;
        this.name = name;
        this.superCategory = superCategory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryDto getSuperCategory() {
        return superCategory;
    }

    public void setSuperCategory(CategoryDto superCategory) {
        this.superCategory = superCategory;
    }
}
