// src/main/java/org/example/secondhandbackend/dto/ProductImageDto.java
package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductImageDto {
    private Long id;
    private String base64Data;
}