package HireCraft.com.SpringBoot.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/webhooks")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event = null;

        try {
            // Verify webhook signature
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            logger.info("Received Stripe webhook event: {}", event.getType());

        } catch (SignatureVerificationException e) {
            logger.error("Invalid signature in webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }

        // Handle the event
        try {
            switch (event.getType()) {
                case "account.updated":
                    handleAccountUpdated(event);
                    break;
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                case "transfer.created":
                    handleTransferCreated(event);
                    break;
                case "payout.paid":
                    handlePayoutPaid(event);
                    break;
                default:
                    logger.info("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok("Webhook handled successfully");

        } catch (Exception e) {
            logger.error("Error handling webhook event {}: {}", event.getType(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling webhook");
        }
    }

    private void handleAccountUpdated(Event event) {
        logger.info("Handling account.updated event");
        // Update provider account status in your database
        // Extract account ID and update onboarding status
    }

    private void handlePaymentIntentSucceeded(Event event) {
        logger.info("Handling payment_intent.succeeded event");
        // Update payment status in your database
        // Send confirmation emails, update order status, etc.
    }

    private void handlePaymentIntentFailed(Event event) {
        logger.info("Handling payment_intent.payment_failed event");
        // Handle failed payment - notify user, update order status
    }

    private void handleTransferCreated(Event event) {
        logger.info("Handling transfer.created event");
        // Track transfers to connected accounts
    }

    private void handlePayoutPaid(Event event) {
        logger.info("Handling payout.paid event");
        // Track when connected accounts receive payouts
    }
}
