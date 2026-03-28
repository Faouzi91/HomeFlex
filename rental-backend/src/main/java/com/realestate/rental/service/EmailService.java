package com.realestate.rental.service;

import com.realestate.rental.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME:noreply@realestate.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void sendVerificationEmail(User user) {
        String verificationLink = frontendUrl + "/auth/verify?token=" + generateVerificationToken(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Verify Your Email - Real Estate Rental");
        message.setText("Hello " + user.getFirstName() + ",\n\n" +
                "Please click the link below to verify your email:\n" +
                verificationLink + "\n\n" +
                "If you didn't create this account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Real Estate Rental Team");

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(User user, String resetToken) {
        String resetLink = frontendUrl + "/auth/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Password Reset - Real Estate Rental");
        message.setText("Hello " + user.getFirstName() + ",\n\n" +
                "You requested to reset your password. Click the link below:\n" +
                resetLink + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Real Estate Rental Team");

        mailSender.send(message);
    }

    public void sendBookingNotificationEmail(User user, String propertyTitle, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("New Booking Request - " + propertyTitle);
        mailMessage.setText("Hello " + user.getFirstName() + ",\n\n" + message);

        mailSender.send(mailMessage);
    }

    private String generateVerificationToken(User user) {
        // Implement token generation logic
        return java.util.UUID.randomUUID().toString();
    }
}
