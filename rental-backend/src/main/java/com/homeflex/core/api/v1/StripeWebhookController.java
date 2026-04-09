package com.homeflex.core.api.v1;

import com.homeflex.core.config.AppProperties;
import com.homeflex.core.domain.entity.ProcessedStripeEvent;
import com.homeflex.core.domain.repository.ProcessedStripeEventRepository;
import com.homeflex.core.service.KycService;
import com.homeflex.features.property.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.identity.VerificationSession;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final KycService kycService;
    private final AppProperties appProperties;
    private final BookingService bookingService;
    private final ProcessedStripeEventRepository processedEventRepository;

    @PostMapping("/stripe")
    @Transactional
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, appProperties.getStripe().getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.warn("Stripe webhook signature verification failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Idempotency: skip events we have already processed. Stripe retries
        // until it sees a 2xx, so we still return 200 OK on a duplicate.
        if (processedEventRepository.existsById(event.getId())) {
            log.info("Stripe event {} already processed — skipping", event.getId());
            return ResponseEntity.ok("Already processed");
        }
        ProcessedStripeEvent record = new ProcessedStripeEvent();
        record.setEventId(event.getId());
        record.setEventType(event.getType());
        processedEventRepository.save(record);

        switch (event.getType()) {
            // ── KYC Identity Verification ──────────────────────────────
            case "identity.verification_session.verified" -> {
                VerificationSession session = deserialize(event, VerificationSession.class);
                if (session != null) {
                    kycService.handleVerified(session.getId());
                } else {
                    log.error("Failed to deserialize VerificationSession for event {}", event.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deserialization failed");
                }
            }
            case "identity.verification_session.requires_input" -> {
                VerificationSession session = deserialize(event, VerificationSession.class);
                if (session != null) {
                    String reason = session.getLastError() != null
                            ? session.getLastError().getCode()
                            : "unknown";
                    kycService.handleRejected(session.getId(), reason);
                } else {
                    log.error("Failed to deserialize VerificationSession for event {}", event.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deserialization failed");
                }
            }

            // ── Payment Intent Events ──────────────────────────────────
            case "payment_intent.succeeded" -> {
                PaymentIntent pi = deserialize(event, PaymentIntent.class);
                if (pi != null) {
                    handlePaymentSucceeded(pi);
                } else {
                    log.error("Failed to deserialize PaymentIntent for event {}", event.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deserialization failed");
                }
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent pi = deserialize(event, PaymentIntent.class);
                if (pi != null) {
                    handlePaymentFailed(pi);
                } else {
                    log.error("Failed to deserialize PaymentIntent for event {}", event.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deserialization failed");
                }
            }

            // ── Connect Account Events ─────────────────────────────────
            case "account.updated" -> {
                Account account = deserialize(event, Account.class);
                if (account != null) {
                    log.info("Connected account updated: accountId={}, chargesEnabled={}, payoutsEnabled={}",
                            account.getId(), account.getChargesEnabled(), account.getPayoutsEnabled());
                }
            }

            default -> log.debug("Unhandled Stripe event type: {}", event.getType());
        }

        return ResponseEntity.ok("OK");
    }

    private void handlePaymentSucceeded(PaymentIntent pi) {
        log.info("Payment succeeded: piId={}, amount={}, transferGroup={}",
                pi.getId(), pi.getAmount(), pi.getTransferGroup());
        bookingService.handlePaymentSucceeded(pi.getId());
    }

    private void handlePaymentFailed(PaymentIntent pi) {
        log.warn("Payment failed: piId={}, transferGroup={}", pi.getId(), pi.getTransferGroup());
        bookingService.handlePaymentFailed(pi.getId());
    }

    @SuppressWarnings("unchecked")
    private <T extends StripeObject> T deserialize(Event event, Class<T> clazz) {
        StripeObject obj = event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);
        if (clazz.isInstance(obj)) {
            return (T) obj;
        }
        log.warn("Could not deserialize {} from event {}", clazz.getSimpleName(), event.getId());
        return null;
    }
}
