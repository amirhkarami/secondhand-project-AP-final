package ir.ac.aut.secondhand.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.client.MultipartBody;
import ir.ac.aut.secondhand.frontend.dto.ProductDto;
import ir.ac.aut.secondhand.frontend.dto.ProductSearchCriteria;
import ir.ac.aut.secondhand.frontend.util.QueryBuilder;
import ir.ac.aut.secondhand.frontend.dto.ProductDetailDto;

import java.nio.file.Path;
import java.util.List;

public final class ProductService {
    private final ApiClient apiClient;

    public ProductService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<ProductDto> getActiveProducts() {
        return apiClient.get("/api/products", new TypeReference<List<ProductDto>>() { }, false);
    }
    public List<ProductDto> search(ProductSearchCriteria criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return getActiveProducts();
        }
        QueryBuilder query = new QueryBuilder("/api/products/search").add("keyword", criteria.getKeyword()).add("categoryId", criteria.getCategoryId()).add("cityId", criteria.getCityId()).add("minPrice", criteria.getMinPrice()).add("maxPrice", criteria.getMaxPrice()).add("sortBy", criteria.getSortBy() == null ? null : criteria.getSortBy().getApiValue());
        return apiClient.get(query.build(), new TypeReference<List<ProductDto>>() { }, false);
    }
    public ProductDetailDto getById(long id) {
        return apiClient.getOptionalAuth(
                "/api/products/" + id,
                new TypeReference<ProductDetailDto>() { }
        );
    }
    public void create(String title, String description, long price, long categoryId, int cityId, List<Path> images) {
        MultipartBody body = new MultipartBody().addText("title", title).addText("description", description == null ? "" : description).addText("price", Long.toString(price)).addText("categoryId", Long.toString(categoryId)).addText("cityId", Integer.toString(cityId));
        if (images != null) {
            images.forEach(path -> body.addFile("images", path));
        }
        apiClient.sendMultipart("POST", "/api/products", body, true);
    }
    public void update(long id, String title, String description, Long price, Long categoryId, Integer cityId, List<Path> images) {
        MultipartBody body = new MultipartBody().addText("title", title).addText("description", description).addText("price", price == null ? null : Long.toString(price)).addText("categoryId", categoryId == null ? null : Long.toString(categoryId)).addText("cityId", cityId == null ? null : Integer.toString(cityId));
        if (images != null) {
            images.forEach(path -> body.addFile("images", path));
        }
        apiClient.sendMultipart("PUT", "/api/products/" + id, body, true);
    }
    public void deleteOwn(long id) {
        apiClient.delete("/api/products/" + id, true);
    }

    public void markSold(long id) {
        apiClient.putEmpty("/api/products/" + id + "/sold", true);
    }
    public void deleteImage(long productId, long imageId) {
        apiClient.delete("/api/products/" + productId + "/images/" + imageId, true);
    }
}