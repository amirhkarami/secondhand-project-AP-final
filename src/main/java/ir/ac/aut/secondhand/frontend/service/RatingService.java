package ir.ac.aut.secondhand.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.dto.RatingSummaryDto;
import ir.ac.aut.secondhand.frontend.dto.request.RatingRequest;


public final class RatingService {
    private final ApiClient apiClient;

    public RatingService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void rate(long productId, int score, String comment) {
        RatingRequest request = new RatingRequest(productId, score, comment == null ? "" : comment);
        apiClient.postJsonText("/api/ratings", request, true);
    }

    public RatingSummaryDto getUserRatings(int userId) {
        return apiClient.get("/api/ratings/" + userId,
                new TypeReference<RatingSummaryDto>() { }, false);
    }
}
