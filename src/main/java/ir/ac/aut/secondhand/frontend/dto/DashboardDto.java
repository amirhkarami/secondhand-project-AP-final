package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardDto {
    private int totalUsers;
    private int totalProducts;
    private int pendingProducts;
    private int activeProducts;
    private int blockedUsers;

    public DashboardDto() {
    }

    public DashboardDto(int totalUsers, int totalProducts, int pendingProducts,
                        int activeProducts, int blockedUsers) {
        this.totalUsers = totalUsers;
        this.totalProducts = totalProducts;
        this.pendingProducts = pendingProducts;
        this.activeProducts = activeProducts;
        this.blockedUsers = blockedUsers;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getPendingProducts() {
        return pendingProducts;
    }

    public void setPendingProducts(int pendingProducts) {
        this.pendingProducts = pendingProducts;
    }

    public int getActiveProducts() {
        return activeProducts;
    }

    public void setActiveProducts(int activeProducts) {
        this.activeProducts = activeProducts;
    }

    public int getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(int blockedUsers) {
        this.blockedUsers = blockedUsers;
    }
}
