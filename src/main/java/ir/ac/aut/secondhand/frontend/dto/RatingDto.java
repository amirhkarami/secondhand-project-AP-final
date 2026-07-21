package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingDto {
    private Long id;
    private int score;
    private String comment;
    private Long productId;

    @JsonAlias({"buyerUsername", "raterUsername"})
    private String buyerUsername;

    @JsonAlias({"createdAt", "ratedAt"})
    private LocalDateTime createdAt;

    public RatingDto() {
    }

    public RatingDto(Long id, int score, String comment, Long productId,
                     String buyerUsername, LocalDateTime createdAt) {
        this.id = id;
        this.score = score;
        this.comment = comment;
        this.productId = productId;
        this.buyerUsername = buyerUsername;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
