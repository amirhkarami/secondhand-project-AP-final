package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ir.ac.aut.secondhand.frontend.model.enums.ReviewResult;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdReviewDto {
    private Long id;
    private ReviewResult result = ReviewResult.UNKNOWN;
    private String note;
    private LocalDateTime reviewedAt;
    private UserDto admin;

    public AdReviewDto() {
    }

    public AdReviewDto(Long id, ReviewResult result, String note,
                       LocalDateTime reviewedAt, UserDto admin) {
        this.id = id;
        this.result = result;
        this.note = note;
        this.reviewedAt = reviewedAt;
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReviewResult getResult() {
        return result;
    }

    public void setResult(ReviewResult result) {
        this.result = result == null ? ReviewResult.UNKNOWN : result;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public UserDto getAdmin() {
        return admin;
    }

    public void setAdmin(UserDto admin) {
        this.admin = admin;
    }
}
