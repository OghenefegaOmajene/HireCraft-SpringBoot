package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentInitiationResponse {
    private boolean success;
    private String reference;
    private String authorizationUrl;
    private String accessCode;
    private String message;

    public PaymentInitiationResponse(boolean success, String reference, String authorizationUrl, String accessCode, String message) {
        this.success = success;
        this.reference = reference;
        this.authorizationUrl = authorizationUrl;
        this.accessCode = accessCode;
        this.message = message;
    }
}