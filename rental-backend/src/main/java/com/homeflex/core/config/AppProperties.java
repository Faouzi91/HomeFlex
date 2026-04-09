package com.homeflex.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Application-wide configuration properties bound from application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Admin admin = new Admin();
    private Aws aws = new Aws();
    private Firebase firebase = new Firebase();
    private Google google = new Google();
    private Stripe stripe = new Stripe();
    private TwilioProperties twilio = new TwilioProperties();
    private Outbox outbox = new Outbox();
    private RateLimit rateLimit = new RateLimit();
    private Monitoring monitoring = new Monitoring();

    @Data
    public static class Jwt {
        private String secret;
        private long expiration;
        private long refreshExpiration;
        private Cookie cookie = new Cookie();

        @Data
        public static class Cookie {
            private String accessTokenName = "ACCESS_TOKEN";
            private String refreshTokenName = "REFRESH_TOKEN";
            private boolean secure;
            private String sameSite = "Strict";
            private int maxAgeSeconds = 604800;
        }
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins;
        private List<String> allowedMethods;
        private List<String> allowedHeaders;
        private List<String> exposedHeaders;
        private boolean allowCredentials;
        private long maxAge;
    }

    @Data
    public static class Admin {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
    }

    @Data
    public static class Aws {
        private S3 s3 = new S3();
        private boolean enabled;

        @Data
        public static class S3 {
            private String accessKey;
            private String secretKey;
            private String region;
            private String bucketName;
            private String endpoint;
        }
    }

    @Data
    public static class Firebase {
        private boolean enabled;
        private String credentialsPath;
    }

    @Data
    public static class Google {
        private String clientId;
        private String clientSecret;
    }

    @Data
    public static class Stripe {
        private String secretKey;
        private String publishableKey;
        private String webhookSecret;
        private double platformCommission;
        private String currency;
    }

    @Data
    public static class TwilioProperties {
        private String accountSid;
        private String authToken;
        private String fromNumber;
        private String fromWhatsApp;
        private boolean enabled;
    }

    @Data
    public static class RateLimit {
        private int authenticatedRequestsPerMinute = 100;
        private int publicRequestsPerMinute = 20;
        private int windowSeconds = 60;
    }

    @Data
    public static class Outbox {
        private boolean relayEnabled = true;
        private int batchSize = 50;
        private long pollIntervalMs = 5000;
        private int maxRetries = 5;
        private long baseBackoffSeconds = 2;
        private String exchangeName = "homeflex.events";
    }

    @Data
    public static class Monitoring {
        private String metricsToken;
    }
}
