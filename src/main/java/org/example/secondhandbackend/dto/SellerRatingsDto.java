
package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SellerRatingsDto {
    private double averageScore;
    private long totalRatings;
    private List<RatingItemDto> ratings;
}