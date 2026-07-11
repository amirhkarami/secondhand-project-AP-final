
package org.example.secondhandbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSummaryDto {
    private int id;
    private String username;
    private String fullName;
    private String type;
    private boolean isActive;
}