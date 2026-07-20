package org.example.secondhandbackend.dto;

import lombok.Getter;

@Getter
public class ProductSummaryDto {
    private int id;
    private String title;
    private long price;
    private String cityName;
    private String categoryName;
    private String status;
    private String imagePath;
    private String ownerUsername;   // ← این خط رو اضافه کن

    public ProductSummaryDto(
            int id,
            String title,
            long price,
            String cityName,
            String categoryName,
            String status,
            String imagePath,
            String ownerUsername      // ← این پارامتر رو اضافه کن
    ){
        this.id = id;
        this.title = title;
        this.price = price;
        this.cityName = cityName;
        this.categoryName = categoryName;
        this.status = status;
        this.imagePath = imagePath;
        this.ownerUsername = ownerUsername;   // ← این خط رو اضافه کن
    }
}