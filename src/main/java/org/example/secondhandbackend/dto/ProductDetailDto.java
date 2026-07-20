
package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductDetailDto {
    private int id;
    private String title;
    private String description;
    private long price;
    private String cityName;
    private String categoryName;
    private String status;
    private String rejectReason;
    private int sellerId;
    private String sellerUsername;
    private String sellerFullName;
    private boolean isOwner;
    private List<ProductImageDto> images;
}