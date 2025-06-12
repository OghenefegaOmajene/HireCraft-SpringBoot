package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.ReviewRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import HireCraft.com.SpringBoot.models.*;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.ReviewRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request, UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get client profile from user email
        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        // Get service provider profile
        ServiceProviderProfile serviceProviderProfile = serviceProviderProfileRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        // Build review
        Review review = Review.builder()
                .ratingNo(request.getRating())
                .reviewTxt(request.getReviewTxt())
                .clientProfile(clientProfile)
                .providerProfile(serviceProviderProfile)
                .build();

        // Save and return response
        Review saved = reviewRepository.save(review);

        return ReviewResponse.builder()
                .rating(saved.getRatingNo())
                .reviewTxt(saved.getReviewTxt())
                .clientFullName(user.getFirstName() + " " + user.getLastName())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return convertToResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getReviewsForProvider(UserDetails userDetails) {
//        List<Review> reviews = reviewRepository.findByProviderProfile_Id(providerId);
//        return convertToResponseList(reviews);
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return reviewRepository.findByProviderProfile_Id(providerProfile.getId())
                .stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        User clientUser = review.getClientProfile().getUser();

        response.setRating(review.getRatingNo());
        response.setReviewTxt(review.getReviewTxt());
        response.setClientFullName(clientUser.getFirstName() + " " + clientUser.getLastName());
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

    @Override
    public List<ReviewResponse> getReviewsByClient(Long clientId) {
        List<Review> reviews = reviewRepository.findByClientProfile_Id(clientId);
        return convertToResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getReviewsByClientForProvider(Long clientId, Long providerId) {
        List<Review> reviews = reviewRepository.findByClientProfile_IdAndProviderProfile_Id(clientId, providerId);
        return convertToResponseList(reviews);
    }

    private List<ReviewResponse> convertToResponseList(List<Review> reviews) {
        return reviews.stream()
                .map(review -> {
                    User clientUser = review.getClientProfile().getUser();
                    return ReviewResponse.builder()
                            .rating(review.getRatingNo())
                            .reviewTxt(review.getReviewTxt())
                            .clientFullName(clientUser.getFirstName() + " " + clientUser.getLastName())
                            .createdAt(review.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}