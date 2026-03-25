package com.realestate.rental.config;

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

    @Data
    public static class Jwt {
        private String secret;
        private long expiration;
        private long refreshExpiration;
        private Cookie cookie = new Cookie();

        @Data
        public static class Cookie {
            private String refreshTokenName;
            private boolean secure;
            private String sameSite;
            private int maxAgeSeconds;
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
}
