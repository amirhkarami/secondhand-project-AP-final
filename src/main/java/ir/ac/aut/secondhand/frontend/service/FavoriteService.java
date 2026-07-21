package ir.ac.aut.secondhand.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;

public final class FavoriteService {
    private final ApiClient apiClient;

    public FavoriteService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void add(long productId) {
        apiClient.postEmpty("/api/favorites/" + productId, true);
    }

    public void remove(long productId) {
        apiClient.delete("/api/favorites/" + productId, true);
    }

    public List<ProductDto> getFavorites() {
        JsonNode response = apiClient.get("/api/favorites", new TypeReference<JsonNode>() { }, true);
        if (response == null || !response.isArray()) {
            return List.of();
        }

        List<ProductDto> products = new ArrayList<>();
        for (JsonNode item : response) {
            JsonNode productNode = item.hasNonNull("product") ? item.get("product")
                    : item.hasNonNull("advertisement") ? item.get("advertisement")
                    : item;
            ProductDto product = apiClient.getObjectMapper().convertValue(productNode, ProductDto.class);
            if (product != null && product.getId() != null) {
                products.add(product);
            }
        }
        return products;
    }
}
