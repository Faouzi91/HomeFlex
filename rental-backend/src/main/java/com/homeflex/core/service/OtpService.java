package com.homeflex.core.service;

import com.homeflex.core.exception.DomainException;
import com.homeflex.core.infrastructure.notification.TwilioSmsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final TwilioSmsGateway twilioSmsGateway;
    
    private static final String OTP_KEY_PREFIX = "otp:";
    private static final int OTP_EXPIRY_MINUTES = 10;

    public void sendOtp(String phoneNumber) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        String key = OTP_KEY_PREFIX + phoneNumber;
        
        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        log.info("Sending OTP {} to {}", otp, phoneNumber);
        
        try {
            twilioSmsGateway.sendSms(phoneNumber, "Your HomeFlex verification code is: " + otp);
        } catch (Exception e) {
            log.error("Failed to send OTP via Twilio", e);
            // In development, we might want to allow this to fail silently or log the OTP
            if (!phoneNumber.startsWith("+1555")) { // Dummy check
                 throw new DomainException("Failed to send verification SMS. Please try again.");
            }
        }
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        String key = OTP_KEY_PREFIX + phoneNumber;
        String cachedOtp = redisTemplate.opsForValue().get(key);
        
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
