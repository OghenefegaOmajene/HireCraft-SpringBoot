package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import HireCraft.com.SpringBoot.models.Review;
import HireCraft.com.SpringBoot.repository.ReviewRepository;
import HireCraft.com.SpringBoot.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return convertToResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getReviewsForProvider(Long providerId) {
        List<Review> reviews = reviewRepository.findByProvider_Id(providerId);
        return convertToResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getReviewsByClient(Long clientId) {
        List<Review> reviews = reviewRepository.findByClient_Id(clientId);
        return convertToResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getReviewsByClientForProvider(Long clientId, Long providerId) {
        List<Review> reviews = reviewRepository.findByClient_IdAndProvider_Id(clientId, providerId);
        return convertToResponseList(reviews);
    }

    private List<ReviewResponse> convertToResponseList(List<Review> reviews) {
        return reviews.stream()
                .map(review -> ReviewResponse.builder()
                        .rating(review.getRatingNo())
                        .reviewTxt(review.getReviewTxt())
                        .build())
                .collect(Collectors.toList());
    }
}
