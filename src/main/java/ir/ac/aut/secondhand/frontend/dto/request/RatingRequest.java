package ir.ac.aut.secondhand.frontend.dto.request;

public class RatingRequest {
    private Long productId;
    private int score;
    private String comment;

    public RatingRequest() {
    }

    public RatingRequest(Long productId, int score, String comment) {
        this.productId = productId;
        this.score = score;
        this.comment = comment;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
