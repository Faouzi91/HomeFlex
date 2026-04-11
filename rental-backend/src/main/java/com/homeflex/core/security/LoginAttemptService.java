package com.homeflex.core.security;

import com.homeflex.core.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;
    private final AppProperties appProperties;

    private static final String ATTEMPTS_KEY_PREFIX = "login_attempts:";
    private static final String BLOCKED_KEY_PREFIX = "login_blocked:";

    public void loginSucceeded(String email) {
        redisTemplate.delete(ATTEMPTS_KEY_PREFIX + email);
    }

    public void loginFailed(String email) {
        String key = ATTEMPTS_KEY_PREFIX + email;
        Long attempts = redisTemplate.opsForValue().increment(key);
        
        if (attempts != null && attempts >= appProperties.getAuth().getMaxFailedAttempts()) {
            blockUser(email);
        } else {
            // Set expiry for attempts counter if not already set (1 hour)
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
    }

    public boolean isBlocked(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLOCKED_KEY_PREFIX + email));
    }

    private void blockUser(String email) {
        int duration = appProperties.getAuth().getLockTimeDurationMinutes();
        redisTemplate.opsForValue().set(BLOCKED_KEY_PREFIX + email, "true", duration, TimeUnit.MINUTES);
        redisTemplate.delete(ATTEMPTS_KEY_PREFIX + email);
        log.warn("User {} has been blocked for {} minutes due to too many failed login attempts", email, duration);
    }
}
