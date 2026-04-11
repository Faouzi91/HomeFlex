package com.homeflex.core.security;

import com.homeflex.core.exception.DomainException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@Converter
public class PiiEncryptionConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    
    // We use a static key derived from JWT_SECRET for prototype simplicity, 
    // but in production, a dedicated KMS key should be used.
    private static byte[] key;

    @Value("${jwt.secret:default-secret-key-that-must-be-at-least-32-chars}")
    public void setSecretKey(String secret) {
        // Pad or truncate to 32 bytes for AES-256
        String paddedSecret = String.format("%-32s", secret).substring(0, 32);
        PiiEncryptionConverter.key = paddedSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Error encrypting PII data", e);
            throw new DomainException("Failed to encrypt sensitive data");
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        // Simple check to prevent trying to decrypt legacy unencrypted data
        if (!isBase64(dbData)) {
            return dbData;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(dbData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // If decryption fails, return original data (handles legacy unencrypted data)
            log.warn("Failed to decrypt PII data, returning raw value");
            return dbData;
        }
    }
    
    private boolean isBase64(String str) {
        return str.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
    }
}
