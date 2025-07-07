package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.PaymentInitiationRequest;
import HireCraft.com.SpringBoot.dtos.requests.WebhookRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentInitiationResponse;
import HireCraft.com.SpringBoot.dtos.response.PaymentTransactionResponse;
import HireCraft.com.SpringBoot.dtos.response.PaymentVerificationResponse;
import HireCraft.com.SpringBoot.models.*;
import HireCraft.com.SpringBoot.services.PaymentService;
import HireCraft.com.SpringBoot.services.PaystackService;
import HireCraft.com.SpringBoot.services.impl.NotificationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaystackService paystackService;
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService, PaystackService paystackService) {
        this.paymentService = paymentService;
        this.paystackService = paystackService;
    }

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
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
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_PROVIDER')")
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
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentTransactionResponse> getTransaction(@PathVariable String reference) {
        try {
            PaymentTransaction transaction = paymentService.getTransactionByReference(reference);
            PaymentTransactionResponse response = PaymentTransactionResponse.fromEntitySafe(transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting transaction: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/client/{clientId}/transactions")
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<PaymentTransactionResponse>> getClientTransactions(@PathVariable Long clientId) {
        try {
            List<PaymentTransaction> transactions = paymentService.getTransactionsByClient(clientId);
            List<PaymentTransactionResponse> response = transactions.stream()
                    .map(PaymentTransactionResponse::fromEntitySafe)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting client transactions: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/provider/{providerId}/transactions")
    @PreAuthorize("hasRole('ROLE_PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<List<PaymentTransactionResponse>> getProviderTransactions(@PathVariable Long providerId) {
        try {
            List<PaymentTransaction> transactions = paymentService.getTransactionsByProvider(providerId);
            List<PaymentTransactionResponse> response = transactions.stream()
                    .map(PaymentTransactionResponse::fromEntitySafe)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
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

    @GetMapping("/ROLE_PROVIDER/{ROLE_PROVIDERId}/earnings")
    @PreAuthorize("hasRole('ROLE_PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getROLE_PROVIDEREarnings(@PathVariable Long ROLE_PROVIDERId) {
        try {
            BigDecimal totalEarnings = paymentService.getTotalEarningsByROLE_PROVIDER(ROLE_PROVIDERId);
            Map<String, Object> response = Map.of(
                    "ROLE_PROVIDERId", ROLE_PROVIDERId,
                    "totalEarnings", totalEarnings,
                    "currency", "NGN"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting ROLE_PROVIDER earnings: ", e);
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