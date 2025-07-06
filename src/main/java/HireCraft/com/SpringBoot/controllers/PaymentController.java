package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.WebhookRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentInitiationResponse;
import HireCraft.com.SpringBoot.dtos.response.PaymentVerificationResponse;
import HireCraft.com.SpringBoot.models.*;
import HireCraft.com.SpringBoot.services.PaymentService;
import HireCraft.com.SpringBoot.services.PaystackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PaystackService paystackService;

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PaymentInitiationResponse> initiatePayment(@Valid @RequestBody PaymentInitiationRequest request) {
        try {
            PaymentInitiationResponse response = paymentService.initiatePayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error initiating payment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentInitiationResponse(false, null, null, null, "Payment initiation failed"));
        }
    }

    @GetMapping("/verify/{reference}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('PROVIDER')")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(@PathVariable String reference) {
        try {
            PaymentVerificationResponse response = paymentService.verifyPayment(reference);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error verifying payment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentVerificationResponse(false, reference, null, "ERROR", null, null, "Payment verification failed"));
        }
    }

    @GetMapping("/transaction/{reference}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentTransaction> getTransaction(@PathVariable String reference) {
        try {
            PaymentTransaction transaction = paymentService.getTransactionByReference(reference);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("Error getting transaction: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/client/{clientId}/transactions")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<PaymentTransaction>> getClientTransactions(@PathVariable Long clientId) {
        try {
            List<PaymentTransaction> transactions = paymentService.getTransactionsByClient(clientId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error getting client transactions: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/provider/{providerId}/transactions")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<List<PaymentTransaction>> getProviderTransactions(@PathVariable Long providerId) {
        try {
            List<PaymentTransaction> transactions = paymentService.getTransactionsByProvider(providerId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error getting provider transactions: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request, @RequestBody String payload) {
        try {
            String signature = request.getHeader("x-paystack-signature");

            if (signature == null || !paystackService.verifyWebhookSignature(payload, signature)) {
                log.warn("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }

            // Parse webhook data
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> webhookData = objectMapper.readValue(payload, Map.class);

            WebhookRequest webhookRequest = new WebhookRequest();
            webhookRequest.setEvent((String) webhookData.get("event"));
            webhookRequest.setData(webhookData.get("data"));

            paymentService.handleWebhook(webhookRequest);

            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Error processing webhook: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }

    @GetMapping("/provider/{providerId}/earnings")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getProviderEarnings(@PathVariable Long providerId) {
        try {
            BigDecimal totalEarnings = paymentService.getTotalEarningsByProvider(providerId);
            Map<String, Object> response = Map.of(
                    "providerId", providerId,
                    "totalEarnings", totalEarnings,
                    "currency", "NGN"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting provider earnings: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/platform/fees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPlatformFees(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        try {
            BigDecimal totalFees = paymentService.getTotalPlatformFeeByDateRange(startDate, endDate);
            Map<String, Object> response = Map.of(
                    "startDate", startDate,
                    "endDate", endDate,
                    "totalFees", totalFees,
                    "currency", "NGN"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting platform fees: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}