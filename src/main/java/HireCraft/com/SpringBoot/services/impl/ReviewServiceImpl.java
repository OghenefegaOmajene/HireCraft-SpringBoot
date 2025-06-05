package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.ReviewRequest;
import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import HireCraft.com.SpringBoot.models.Review;
import HireCraft.com.SpringBoot.models.User;
import HireCraft.com.SpringBoot.repository.ReviewRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        User provider = userRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        Review review = Review.builder()
                .ratingNo(request.getRating())
                .reviewTxt(request.getReviewTxt())
                .client(client)
                .provider(provider)
                .build();

        Review saved = reviewRepository.save(review);

        return ReviewResponse.builder()
                .rating(saved.getRatingNo())
                .reviewTxt(saved.getReviewTxt())
                .clientName(client.getFirstName())
                .clientName(client.getLastName())
                .createdAt(saved.getCreatedAt())
                .build();
    }

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
