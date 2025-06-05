package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import HireCraft.com.SpringBoot.services.ReviewService;
import HireCraft.com.SpringBoot.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 1. Get all reviews ever
    @GetMapping("/all")
    public List<ReviewResponse> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // 2. Get all reviews for a provider
    @GetMapping("/provider/{providerId}")
    public List<ReviewResponse> getReviewsForProvider(@PathVariable Long providerId) {
        return reviewService.getReviewsForProvider(providerId);
    }

    // 3. Get all reviews written by a client
    @GetMapping("/client/{clientId}")
    public List<ReviewResponse> getReviewsByClient(@PathVariable Long clientId) {
        return reviewService.getReviewsByClient(clientId);
    }

    // 4. Get all reviews a client has written for a specific provider
    @GetMapping("/client/{clientId}/provider/{providerId}")
    public List<ReviewResponse> getReviewsByClientForProvider(@PathVariable Long clientId, @PathVariable Long providerId) {
        return reviewService.getReviewsByClientForProvider(clientId, providerId);
    }
}
