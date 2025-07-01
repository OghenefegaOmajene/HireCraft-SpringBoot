package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentResponse;
import HireCraft.com.SpringBoot.services.PaymentService;
import HireCraft.com.SpringBoot.dtos.PaymentBreakdown;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Validated
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/processPayment")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<PaymentResponse>> getClientPayments(@PathVariable Long clientId) {
        List<PaymentResponse> payments = paymentService.getClientPayments(clientId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<PaymentResponse>> getProviderPayments(@PathVariable Long providerId) {
        List<PaymentResponse> payments = paymentService.getProviderPayments(providerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/breakdown")
    public ResponseEntity<PaymentBreakdown> getPaymentBreakdown(@RequestParam BigDecimal amount) {
        PaymentBreakdown breakdown = paymentService.calculatePaymentBreakdown(amount);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/provider/{providerId}/earnings")
    public ResponseEntity<BigDecimal> getProviderEarnings(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        BigDecimal earnings = paymentService.calculateProviderEarnings(providerId, startDate, endDate);
        return ResponseEntity.ok(earnings);
    }

    @GetMapping("/platform/revenue")
    public ResponseEntity<BigDecimal> getPlatformRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        BigDecimal revenue = paymentService.calculatePlatformRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}
