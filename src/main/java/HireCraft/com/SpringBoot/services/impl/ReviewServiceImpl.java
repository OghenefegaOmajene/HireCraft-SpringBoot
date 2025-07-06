package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.NotificationRequest;
import HireCraft.com.SpringBoot.dtos.requests.ReviewRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import HireCraft.com.SpringBoot.enums.BookingStatus;
import HireCraft.com.SpringBoot.enums.NotificationType;
import HireCraft.com.SpringBoot.enums.ReferenceType;
import HireCraft.com.SpringBoot.models.*;
import HireCraft.com.SpringBoot.repository.*;
import HireCraft.com.SpringBoot.services.NotificationService;
import HireCraft.com.SpringBoot.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;
    private final NotificationService notificationService;
    private final BookingRepository bookingRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, ClientProfileRepository clientProfileRepository, ServiceProviderProfileRepository serviceProviderProfileRepository, NotificationService notificationService, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.serviceProviderProfileRepository = serviceProviderProfileRepository;
        this.notificationService = notificationService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ReviewResponse createReview(ReviewRequest request, UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get client profile from user email
        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        // Get the booking by ID
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Validate that the booking belongs to the authenticated client
        if (!booking.getClientProfile().getId().equals(clientProfile.getId())) {
            throw new RuntimeException("You can only review your own bookings");
        }

        // Validate that the booking is completed
        if (!booking.getStatus().equals(BookingStatus.COMPLETED)) {
            throw new RuntimeException("You can only review completed bookings");
        }

        // Check if review already exists for this booking
        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new RuntimeException("A review already exists for this booking");
        }

        // Get service provider profile from the booking
        ServiceProviderProfile serviceProviderProfile = booking.getProviderProfile();

        // Build review
        Review review = Review.builder()
                .ratingNo(request.getRating())
                .reviewTxt(request.getReviewTxt())
                .clientProfile(clientProfile)
                .providerProfile(serviceProviderProfile)
                .booking(booking) // Link to the booking
                .build();

        // Save and return response
        Review savedReview = reviewRepository.save(review);
        updateServiceProviderAverageRating(serviceProviderProfile);

        Long providerUserId = serviceProviderProfile.getUser().getId();

        // Create a notification for the provider
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .message(String.format("New %.1f star review received from %s.",
                        savedReview.getRatingNo(),
                        clientProfile.getUser().getFirstName() + " " + clientProfile.getUser().getLastName())) // Include service type
                .type(NotificationType.REVIEW_RECEIVED)
                .userId(providerUserId)
                .referenceId(savedReview.getId())
                .referenceType(ReferenceType.REVIEW)
                .build();
        notificationService.createNotification(notificationRequest);

        return ReviewResponse.builder()
                .rating(savedReview.getRatingNo())
                .reviewTxt(savedReview.getReviewTxt())
                .clientFullName(user.getFirstName() + " " + user.getLastName())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    @Transactional
    private void updateServiceProviderAverageRating(ServiceProviderProfile serviceProviderProfile) {
        // Fetch all reviews for this specific service provider
        List<Review> reviews = reviewRepository.findByProviderProfile_Id(serviceProviderProfile.getId());

        if (reviews.isEmpty()) {
            serviceProviderProfile.setAverageRating(0.0); // No reviews, so average is 0
        } else {
            // Calculate the sum of all ratings
            double sumOfRatings = reviews.stream()
                    .mapToDouble(Review::getRatingNo) // Use getRatingNo from your Review model
                    .sum();

            // Calculate the raw new average
            double newAverage = sumOfRatings / reviews.size();

            double roundedAverage = Math.round(newAverage * 10.0) / 10.0;

            serviceProviderProfile.setAverageRating(roundedAverage);
        }

        serviceProviderProfileRepository.save(serviceProviderProfile);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return convertToResponseList(reviews);
    }

    // Updated methods for ReviewServiceImpl

    @Override
    public List<ReviewResponse> getReviewsForProvider(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return reviewRepository.findByProviderProfile_IdOrderByCreatedAtDesc(providerProfile.getId())
                .stream()
                .map(review -> mapToReviewResponseForProvider(review)) // Use specific method for provider
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByClient(UserDetails userDetails) {
        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        return reviewRepository.findByClientProfile_IdOrderByCreatedAtDesc(clientProfile.getId())
                .stream()
                .map(review -> mapToReviewResponseForClient(review)) // Use specific method for client
                .collect(Collectors.toList());
    }

    // Method for mapping reviews when fetched by a provider (shows client info)
    private ReviewResponse mapToReviewResponseForProvider(Review review) {
        ReviewResponse response = new ReviewResponse();
        User clientUser = review.getClientProfile().getUser();

        response.setRating(review.getRatingNo());
        response.setReviewTxt(review.getReviewTxt());
        response.setClientFullName(clientUser.getFirstName() + " " + clientUser.getLastName());
        response.setProviderFullName(null); // Not needed when provider fetches reviews

        // Set client's profile picture - assuming you have a profilePictureUrl field in User model
        response.setProfilePictureUrl(clientUser.getProfilePictureUrl()); // Make sure this field exists in User model
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

    // Method for mapping reviews when fetched by a client (shows provider info)
    private ReviewResponse mapToReviewResponseForClient(Review review) {
        ReviewResponse response = new ReviewResponse();
        User clientUser = review.getClientProfile().getUser();
        User providerUser = review.getProviderProfile().getUser();

        response.setRating(review.getRatingNo());
        response.setReviewTxt(review.getReviewTxt());
        response.setClientFullName(null);
        response.setProviderFullName(providerUser.getFirstName() + " " + providerUser.getLastName());

        // Set provider's profile picture when client fetches reviews
        response.setProfilePictureUrl(providerUser.getProfilePictureUrl()); // Make sure this field exists in User model
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

// Remove the old generic mapToReviewResponse method since we now have specific methods

    @Override
    public List<ReviewResponse> getReviewsByClientForProvider(Long clientId, Long providerId) {
        List<Review> reviews = reviewRepository.findByClientProfile_IdAndProviderProfile_Id(clientId, providerId);
        return convertToResponseList(reviews);
    }

    @Override
    public long getReviewCountForProvider(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        // This is the core logic: count reviews by provider profile ID
        return reviewRepository.countByProviderProfile_Id(providerProfile.getId());
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