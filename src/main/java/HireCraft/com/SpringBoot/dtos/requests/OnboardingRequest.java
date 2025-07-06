package HireCraft.com.SpringBoot.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OnboardingRequest {
    @NotNull(message = "Provider ID is required")
    private Long providerId;

    private String refreshUrl;
    private String returnUrl;

    public @NotNull(message = "Provider ID is required") Long getProviderId() {
        return providerId;
    }

    public String getRefreshUrl() {
        return refreshUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }
}
