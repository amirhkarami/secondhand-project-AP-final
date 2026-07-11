
package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardDto {
    private long totalUsers;
    private long totalProducts;
    private long pendingProducts;
    private long activeProducts;
    private long blockedUsers;
}