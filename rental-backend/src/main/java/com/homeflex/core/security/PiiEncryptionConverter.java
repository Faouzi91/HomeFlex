package com.homeflex.core.security;

import com.homeflex.core.exception.DomainException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Attribute converter that encrypts/decrypts PII data in the database.
 * Uses AES/GCM/NoPadding (Authenticated Encryption) for high security.
 */
@Slf4j
@Component
@Converter
public class PiiEncryptionConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    // Dedicated key for PII encryption, MUST be distinct from JWT secret.
    private static byte[] key;

    @Value("${app.security.pii-encryption-key:default-32-chars-pii-encrypt-key-!}")
    public void setSecretKey(String secret) {
        // Pad or truncate to 32 bytes for AES-256
        String paddedSecret = String.format("%-32s", secret).substring(0, 32);
        PiiEncryptionConverter.key = paddedSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) {
            return attribute;
        }
        try {
            byte[] iv = new byte[IV_LENGTH_BYTE];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and Ciphertext: [IV (12 bytes)][Ciphertext (variable)]
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedBytes);
            
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("Error encrypting PII data", e);
            throw new DomainException("Failed to protect sensitive data");
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return dbData;
        }

        try {
            byte[] combined = Base64.getDecoder().decode(dbData);
            
            // Validate data length (at least IV + tag overhead)
            if (combined.length < IV_LENGTH_BYTE + (TAG_LENGTH_BIT / 8)) {
                 log.warn("PII data too short for decryption, returning raw value (possible legacy data)");
                 return dbData;
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(combined);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);
            
            byte[] encryptedBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedBytes);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Decryption failure might be due to legacy data or wrong key
            log.warn("Failed to decrypt PII data (using {}), returning raw value. Reason: {}", ALGORITHM, e.getMessage());
            return dbData;
        }
    }
}
