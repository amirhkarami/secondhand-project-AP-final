
package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RatingItemDto {
    private int score;
    private String comment;
    private String buyerUsername;
}