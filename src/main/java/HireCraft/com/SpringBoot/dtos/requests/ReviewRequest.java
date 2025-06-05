package HireCraft.com.SpringBoot.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    @NotBlank(message = "Rating must be at least 1 star")
    @Size(min = 1, max = 5, message = "Rating must be at least 1 star and cannot be more than 5 stars")
    private String rating;

    @NotBlank(message = "Enter your country")
    private String reviewTxt;
}
