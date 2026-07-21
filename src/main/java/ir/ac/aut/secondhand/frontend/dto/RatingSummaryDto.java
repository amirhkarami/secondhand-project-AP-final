package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingSummaryDto {
    private double averageScore;
    private int totalRatings;
    private List<RatingDto> ratings = new ArrayList<>();

    public RatingSummaryDto() {
    }

    public RatingSummaryDto(double averageScore, int totalRatings, List<RatingDto> ratings) {
        this.averageScore = averageScore;
        this.totalRatings = totalRatings;
        this.ratings = ratings == null ? new ArrayList<>() : new ArrayList<>(ratings);
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public List<RatingDto> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingDto> ratings) {
        this.ratings = ratings == null ? new ArrayList<>() : new ArrayList<>(ratings);
    }
}
