package com.homeflex.core.service;

import com.homeflex.core.domain.entity.EmailVerificationToken;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.EmailVerificationTokenRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final CircuitBreaker emailCircuitBreaker;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Value("${MAIL_USERNAME:noreply@realestate.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void sendVerificationEmail(User user) {
        String token = generateVerificationToken(user);
        String verificationLink = frontendUrl + "/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Verify Your Email - HomeFlex");
        message.setText("Hello " + user.getFirstName() + ",\n\n" +
                "Please click the link below to verify your email:\n" +
                verificationLink + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you didn't create this account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "HomeFlex Team");

        sendWithCircuitBreaker(message, "verification", user.getEmail());
    }

    public void sendPasswordResetEmail(User user, String resetToken) {
        String resetLink = frontendUrl + "/auth/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Password Reset - HomeFlex");
        message.setText("Hello " + user.getFirstName() + ",\n\n" +
                "You requested to reset your password. Click the link below:\n" +
                resetLink + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "HomeFlex Team");

        sendWithCircuitBreaker(message, "password-reset", user.getEmail());
    }

    public void sendBookingNotificationEmail(User user, String propertyTitle, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("New Booking Request - " + propertyTitle);
        mailMessage.setText("Hello " + user.getFirstName() + ",\n\n" + message);

        sendWithCircuitBreaker(mailMessage, "booking-notification", user.getEmail());
    }

    private void sendWithCircuitBreaker(SimpleMailMessage message, String emailType, String recipient) {
        try {
            emailCircuitBreaker.executeRunnable(() -> mailSender.send(message));
            log.info("Email sent (type={}, to={})", emailType, recipient);
        } catch (Exception e) {
            log.warn("Email delivery failed (type={}, to={}): {}. Circuit breaker state: {}",
                    emailType, recipient, e.getMessage(), emailCircuitBreaker.getState());
            throw e;
        }
    }

    /**
     * Creates a persisted email verification token with 24-hour expiry.
     */
    private String generateVerificationToken(User user) {
        // Remove any existing tokens for this user
        emailVerificationTokenRepository.deleteByUserId(user.getId());

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        emailVerificationTokenRepository.save(token);

        return token.getToken();
    }
}
