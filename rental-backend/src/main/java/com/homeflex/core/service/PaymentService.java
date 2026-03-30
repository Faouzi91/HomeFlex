package com.homeflex.core.service;

import com.homeflex.core.config.AppProperties;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Balance;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Transfer;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.TransferCreateParams;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final Retry stripeRetry;

    @PostConstruct
    public void init() {
        Stripe.apiKey = appProperties.getStripe().getSecretKey();
    }

    // ── Stripe Connect: Account Onboarding ─────────────────────────────

    /**
     * Creates a Stripe Express connected account for a landlord and returns
     * an Account Link URL so they can complete onboarding in Stripe's hosted UI.
     */
    @Transactional
    public ConnectOnboardingResponse createConnectedAccount(UUID userId, String refreshUrl, String returnUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getStripeAccountId() != null) {
            // Account already exists — generate a fresh onboarding link
            // (user may need to complete remaining requirements)
            return new ConnectOnboardingResponse(
                    user.getStripeAccountId(),
                    createAccountLink(user.getStripeAccountId(), refreshUrl, returnUrl));
        }

        try {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setEmail(user.getEmail())
                    .setCountry("US")
                    .putMetadata("user_id", userId.toString())
                    .setCapabilities(AccountCreateParams.Capabilities.builder()
                            .setTransfers(AccountCreateParams.Capabilities.Transfers.builder()
                                    .setRequested(true)
                                    .build())
                            .build())
                    .build();

            Account account = Account.create(params);

            user.setStripeAccountId(account.getId());
            userRepository.save(user);

            String onboardingUrl = createAccountLink(account.getId(), refreshUrl, returnUrl);

            log.info("Stripe Connect account created: accountId={}, userId={}", account.getId(), userId);
            return new ConnectOnboardingResponse(account.getId(), onboardingUrl);
        } catch (StripeException e) {
            log.error("Failed to create Stripe Connect account for user {}", userId, e);
            throw new DomainException("Unable to create payment account. Please try again later.");
        }
    }

    private String createAccountLink(String accountId, String refreshUrl, String returnUrl) {
        try {
            AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                    .setAccount(accountId)
                    .setRefreshUrl(refreshUrl)
                    .setReturnUrl(returnUrl)
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            AccountLink link = AccountLink.create(params);
            return link.getUrl();
        } catch (StripeException e) {
            log.error("Failed to create account link for account {}", accountId, e);
            throw new DomainException("Unable to generate onboarding link.");
        }
    }

    // ── Destination Charges (Separate Charges + Transfers for Escrow) ──

    /**
     * Creates a PaymentIntent on the platform account. Funds are collected
     * from the tenant immediately but held on the platform (escrow) until
     * explicitly released via {@link #releaseEscrow}.
     * <p>
     * Uses the "Separate Charges and Transfers" pattern so that the platform
     * controls when funds move to the landlord's connected account.
     *
     * @param amount        Total booking amount
     * @param currency      Currency code (e.g., "xaf")
     * @param description   Human-readable description
     * @param transferGroup A unique group ID (e.g., "booking_{id}") linking
     *                      the charge to its future transfer
     * @return The created PaymentIntent (caller stores its ID on the booking)
     */
    public PaymentIntent createBookingPaymentIntent(BigDecimal amount, String currency,
                                                     String description, String transferGroup) {
        try {
            return Retry.decorateSupplier(stripeRetry, () -> {
                try {
                    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                            .setAmount(toStripeAmount(amount))
                            .setCurrency(currency)
                            .setDescription(description)
                            .setTransferGroup(transferGroup)
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build())
                            .build();
                    return PaymentIntent.create(params);
                } catch (StripeException e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (Exception e) {
            log.error("Failed to create booking PaymentIntent after retries: {}", description, e);
            return null;
        }
    }

    /**
     * Releases escrowed funds by creating a Transfer from the platform account
     * to the landlord's connected account. The platform keeps its commission;
     * only the landlord's share is transferred.
     *
     * @param landlordStripeAccountId The landlord's connected account ID
     * @param totalAmount             The original booking total
     * @param currency                Currency code
     * @param transferGroup           Must match the charge's transfer_group
     * @return The created Transfer
     */
    public Transfer releaseEscrow(String landlordStripeAccountId, BigDecimal totalAmount,
                                  String currency, String transferGroup) {
        BigDecimal platformFee = computePlatformFee(totalAmount);
        BigDecimal landlordAmount = totalAmount.subtract(platformFee);

        try {
            return Retry.decorateSupplier(stripeRetry, () -> {
                try {
                    TransferCreateParams params = TransferCreateParams.builder()
                            .setAmount(toStripeAmount(landlordAmount))
                            .setCurrency(currency)
                            .setDestination(landlordStripeAccountId)
                            .setTransferGroup(transferGroup)
                            .build();

                    Transfer transfer = Transfer.create(params);
                    log.info("Escrow released: transferGroup={}, landlordAmount={}, platformFee={}",
                            transferGroup, landlordAmount, platformFee);
                    return transfer;
                } catch (StripeException e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (Exception e) {
            log.error("Failed to release escrow after retries for transferGroup={}", transferGroup, e);
            return null;
        }
    }

    // ── Balance & Payouts ──────────────────────────────────────────────

    /**
     * Retrieves the balance of a landlord's connected Stripe account.
     * Returns available and pending amounts per currency.
     */
    public Balance getConnectedAccountBalance(String stripeAccountId) {
        try {
            return Retry.decorateSupplier(stripeRetry, () -> {
                try {
                    com.stripe.param.BalanceRetrieveParams params =
                            com.stripe.param.BalanceRetrieveParams.builder().build();

                    com.stripe.net.RequestOptions options = com.stripe.net.RequestOptions.builder()
                            .setStripeAccount(stripeAccountId)
                            .build();

                    return Balance.retrieve(params, options);
                } catch (StripeException e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (Exception e) {
            log.error("Failed to retrieve balance after retries for account {}", stripeAccountId, e);
            return null;
        }
    }

    // ── Fee Calculation ────────────────────────────────────────────────

    public BigDecimal computePlatformFee(BigDecimal totalAmount) {
        double commissionRate = appProperties.getStripe().getPlatformCommission();
        return totalAmount
                .multiply(BigDecimal.valueOf(commissionRate))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private long toStripeAmount(BigDecimal amount) {
        return amount.multiply(new BigDecimal(100)).longValue();
    }

    // ── Response Records ───────────────────────────────────────────────

    public record ConnectOnboardingResponse(String stripeAccountId, String onboardingUrl) {}
}
