package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.ReviewRequest;
import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);

    List<ReviewResponse> getAllReviews();  // All reviews in app

    List<ReviewResponse> getReviewsForProvider(Long providerId); // All reviews for one provider

    List<ReviewResponse> getReviewsByClient(Long clientId); // All reviews written by a specific client

    List<ReviewResponse> getReviewsByClientForProvider(Long clientId, Long providerId); // A client’s review(s) for a specific provider
}
