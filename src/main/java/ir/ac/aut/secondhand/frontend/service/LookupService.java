package ir.ac.aut.secondhand.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.dto.CategoryDto;
import ir.ac.aut.secondhand.frontend.dto.CityDto;
import ir.ac.aut.secondhand.frontend.dto.request.CategoryCreateRequest;
import ir.ac.aut.secondhand.frontend.dto.request.CityCreateRequest;

import java.util.List;

public final class LookupService {
    private final ApiClient apiClient;

    public LookupService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<CategoryDto> getCategories() {
        return apiClient.get("/categories", new TypeReference<List<CategoryDto>>() { }, false);
    }

    public List<CityDto> getCities() {
        return apiClient.get("/cities", new TypeReference<List<CityDto>>() { }, false);
    }

    public void createCategory(Long id, String name, CategoryDto superCategory) {
        CategoryDto parentReference = superCategory == null
                ? null
                : new CategoryDto(superCategory.getId(), null, null);
        apiClient.postJsonText("/categories",
                new CategoryCreateRequest(id, name, parentReference), true);
    }

    public void deleteCategory(long categoryId) {
        apiClient.delete("/categories/" + categoryId, true);
    }

    public void createCity(String name) {
        apiClient.postJsonText("/cities", new CityCreateRequest(name), true);
    }

    public void deleteCity(int cityId) {
        apiClient.delete("/cities/" + cityId, true);
    }
}
