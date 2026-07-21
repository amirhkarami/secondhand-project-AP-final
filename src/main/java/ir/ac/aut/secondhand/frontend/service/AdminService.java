package ir.ac.aut.secondhand.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.dto.DashboardDto;
import ir.ac.aut.secondhand.frontend.dto.ProductDto;
import ir.ac.aut.secondhand.frontend.dto.UserDto;
import ir.ac.aut.secondhand.frontend.dto.request.RejectProductRequest;

import java.util.List;

public final class AdminService {
    private final ApiClient apiClient;

    public AdminService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public DashboardDto getDashboard() {
        return apiClient.get("/api/admin/dashboard", new TypeReference<DashboardDto>() { }, true);
    }

    public List<ProductDto> getPendingProducts() {
        return apiClient.get("/api/admin/products/pending", new TypeReference<List<ProductDto>>() { }, true);
    }

    public void approveProduct(long productId) {
        apiClient.putEmpty("/api/admin/products/" + productId + "/approve", true);
    }

    public void rejectProduct(long productId, String reason) {
        apiClient.putJsonText("/api/admin/products/" + productId + "/reject",
                new RejectProductRequest(reason), true);
    }

    public void deleteProduct(long productId) {
        apiClient.delete("/api/admin/products/" + productId, true);
    }

    public List<UserDto> getUsers() {
        return apiClient.get("/api/admin/users", new TypeReference<List<UserDto>>() { }, true);
    }

    public void blockUser(int userId) {
        apiClient.putEmpty("/api/admin/users/" + userId + "/block", true);
    }

    public void unblockUser(int userId) {
        apiClient.putEmpty("/api/admin/users/" + userId + "/unblock", true);
    }

    public void promoteUser(int userId) {
        apiClient.putEmpty("/api/admin/users/" + userId + "/promote", true);
    }
}
