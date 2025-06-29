package HireCraft.com.SpringBoot.config;

import jakarta.annotation.PostConstruct;
import lombok.Value;

@Component
@Slf4j
public class StripePaymentConfig {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public StripePaymentResult processPayment(String paymentMethodId,
                                              BigDecimal totalAmount,
                                              BigDecimal platformFee,
                                              Long providerId) {
        try {
            // Convert to cents for Stripe
            long amountInCents = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();
            long platformFeeInCents = platformFee.multiply(BigDecimal.valueOf(100)).longValue();

            // Create payment intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .setApplicationFeeAmount(platformFeeInCents)
                    .putMetadata("provider_id", providerId.toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            log.info("Stripe payment intent created: {}", intent.getId());

            return new StripePaymentResult(true, intent.getId(), intent.getStatus());

        } catch (StripeException e) {
            log.error("Stripe payment failed", e);
            return new StripePaymentResult(false, null, e.getMessage());
        }
    }
}
