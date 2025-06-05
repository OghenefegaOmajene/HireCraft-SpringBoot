package HireCraft.com.SpringBoot.dtos.requests;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    @DecimalMin(value = "1.0", message = "Rating must be at least 1 star")
    @DecimalMax(value = "5.0", message = "Rating cannot be more than 5 stars")
    private Double rating;

    @NotBlank(message = "Enter a review")
    private String reviewTxt;
}
