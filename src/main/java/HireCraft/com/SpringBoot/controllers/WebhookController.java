package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.enums.TransactionStatus;
import HireCraft.com.SpringBoot.models.Transaction;
import HireCraft.com.SpringBoot.repository.TransactionRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentFailed(event);
                break;
            case "payment_intent.processing":
                handlePaymentProcessing(event);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok("Success");
    }

    private void handlePaymentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            String transactionId = paymentIntent.getMetadata().get("transaction_id");
            if (transactionId != null) {
                Transaction transaction = transactionRepository.findById(Long.parseLong(transactionId)).orElse(null);
                if (transaction != null) {
                    transaction.setStatus(TransactionStatus.COMPLETED);
                    transactionRepository.save(transaction);
                }
            }
        }
    }

    private void handlePaymentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            String transactionId = paymentIntent.getMetadata().get("transaction_id");
            if (transactionId != null) {
                Transaction transaction = transactionRepository.findById(Long.parseLong(transactionId)).orElse(null);
                if (transaction != null) {
                    transaction.setStatus(TransactionStatus.FAILED);
                    transactionRepository.save(transaction);
                }
            }
        }
    }

    private void handlePaymentProcessing(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            String transactionId = paymentIntent.getMetadata().get("transaction_id");
            if (transactionId != null) {
                Transaction transaction = transactionRepository.findById(Long.parseLong(transactionId)).orElse(null);
                if (transaction != null) {
                    transaction.setStatus(TransactionStatus.PROCESSING);
                    transactionRepository.save(transaction);
                }
            }
        }
    }
}