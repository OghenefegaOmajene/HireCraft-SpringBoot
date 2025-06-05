package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> getAllReviews();
    ReviewResponse getProviderReviews(Long reviewId, Long userId);
    ReviewResponse getClientReviews(Long reviewId, Long userId);
}
