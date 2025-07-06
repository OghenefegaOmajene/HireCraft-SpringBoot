package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.OnboardingRequest;
import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
import HireCraft.com.SpringBoot.services.PaymentService;
import com.stripe.model.Balance;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Create Connect account for service provider
     * @param request OnboardingRequest containing provider details
     * @return ResponseEntity with account creation result
     */
    @PostMapping("/connect-account")
    public ResponseEntity<?> createConnectAccount(@Valid @RequestBody OnboardingRequest request) {
        try {
            Map<String, Object> response = paymentService.createConnectAccount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * Create payment intent for transaction
     * @param request PaymentRequest containing payment details
     * @return ResponseEntity with payment intent result
     */
    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@Valid @RequestBody PaymentRequest request) {
        try {
            Map<String, Object> response = paymentService.createPaymentIntent(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * Get platform balance (collected fees)
     * @return ResponseEntity with balance information
     */
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance() {
        try {
            Balance balance = paymentService.getAccountBalance();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("balance", balance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * Get Connect account status and details
     * @param accountId Stripe Connect account ID
     * @return ResponseEntity with account details
     */
    @GetMapping("/connect-account/{accountId}")
    public ResponseEntity<?> getConnectAccount(@PathVariable String accountId) {
        try {
            Map<String, Object> response = paymentService.getConnectAccountDetails(accountId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * Check provider onboarding status
     * @param providerId Service provider ID
     * @return ResponseEntity with onboarding status
     */
    @GetMapping("/provider/{providerId}/onboarding-status")
    public ResponseEntity<?> getProviderOnboardingStatus(@PathVariable Long providerId) {
        try {
            Map<String, Object> response = paymentService.getProviderOnboardingStatus(providerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * Helper method to build consistent error responses
     * @param errorMessage Error message to include
     * @return ResponseEntity with error response
     */
    private ResponseEntity<?> buildErrorResponse(String errorMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", errorMessage);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}