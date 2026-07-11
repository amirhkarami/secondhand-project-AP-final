
package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSummaryDto {
    private int id;
    private String title;
    private long price;
    private String cityName;
    private String categoryName;
    private String status;
}