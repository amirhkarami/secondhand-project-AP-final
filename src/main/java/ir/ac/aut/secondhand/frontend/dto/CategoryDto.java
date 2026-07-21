package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDto {
    private Long id;
    private String name;

    @JsonAlias({"superCategory", "parent", "parentCategory"})
    private CategoryDto superCategory;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String name, CategoryDto superCategory) {
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

    @Override
    public String toString() {
        return name == null ? "Unnamed category" : name;
    }
}
