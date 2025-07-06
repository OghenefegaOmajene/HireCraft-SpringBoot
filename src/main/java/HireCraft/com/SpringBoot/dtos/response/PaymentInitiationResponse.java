package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationResponse {
    private boolean success;
    private String reference;
    private String authorizationUrl;
    private String accessCode;
    private String message;
}