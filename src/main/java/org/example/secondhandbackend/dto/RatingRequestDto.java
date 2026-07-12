// dto/RatingRequestDto.java
package org.example.secondhandbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequestDto {
    private int productId;
    private int score;
    private String comment;
}