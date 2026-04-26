package com.homeflex.features.property.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Returned by POST /bookings/{id}/pay — everything the frontend needs
 * to present the Stripe payment sheet.
 */
public record PaymentInitiationResponse(
        UUID bookingId,
        String stripeClientSecret,
        String stripePaymentIntentId,
        BigDecimal amount,
        String currency
) {}
