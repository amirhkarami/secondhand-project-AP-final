package ir.ac.aut.secondhand.frontend.dto;

import ir.ac.aut.secondhand.frontend.model.enums.SortOption;

public class ProductSearchCriteria {
    private String keyword;
    private Long categoryId;
    private Integer cityId;
    private Long minPrice;
    private Long maxPrice;
    private SortOption sortBy;

    public ProductSearchCriteria() {
    }

    public ProductSearchCriteria(String keyword, Long categoryId, Integer cityId,
                                 Long minPrice, Long maxPrice, SortOption sortBy) {
        this.keyword = keyword;
        this.categoryId = categoryId;
        this.cityId = cityId;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.sortBy = sortBy;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public SortOption getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortOption sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isEmpty() {
        return (keyword == null || keyword.isBlank())
                && categoryId == null
                && cityId == null
                && minPrice == null
                && maxPrice == null
                && sortBy == null;
    }
}
