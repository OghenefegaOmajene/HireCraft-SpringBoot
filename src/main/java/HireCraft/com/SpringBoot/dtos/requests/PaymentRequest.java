package HireCraft.com.SpringBoot.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {
    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least $1.00")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    public @NotNull(message = "Client ID is required") Long getClientId() {
        return clientId;
    }

    public @NotNull(message = "Provider ID is required") Long getProviderId() {
        return providerId;
    }

    public @NotNull(message = "Amount is required") @DecimalMin(value = "1.00", message = "Amount must be at least $1.00") BigDecimal getAmount() {
        return amount;
    }

    public @NotBlank(message = "Description is required") @Size(max = 500, message = "Description cannot exceed 500 characters") String getDescription() {
        return description;
    }
}