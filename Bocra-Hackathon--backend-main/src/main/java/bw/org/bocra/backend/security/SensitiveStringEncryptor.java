package bw.org.bocra.backend.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Converter
public class SensitiveStringEncryptor implements AttributeConverter<String, String> {

    private static final String PREFIX = "enc::";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final boolean ENCRYPTION_ENABLED =
            !"false".equalsIgnoreCase(System.getenv().getOrDefault("APP_ENCRYPTION_ENABLED", "true"));

    private static final SecretKeySpec SECRET_KEY = buildKey();

    private static SecretKeySpec buildKey() {
        String keyMaterial = System.getenv("APP_ENCRYPTION_SECRET");
        if (keyMaterial == null || keyMaterial.isBlank()) {
            keyMaterial = "dev-only-change-this-encryption-secret-immediately";
            log.warn("APP_ENCRYPTION_SECRET is not set. Using development fallback encryption key.");
        }

        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(keyMaterial.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(hash, "AES");
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize encryption key", ex);
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank() || !ENCRYPTION_ENABLED) {
            return attribute;
        }

        if (attribute.startsWith(PREFIX)) {
            return attribute;
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);

            return PREFIX + Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to encrypt value", ex);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank() || !ENCRYPTION_ENABLED) {
            return dbData;
        }

        if (!dbData.startsWith(PREFIX)) {
            return dbData;
        }

        try {
            byte[] payload = Base64.getDecoder().decode(dbData.substring(PREFIX.length()));
            ByteBuffer buffer = ByteBuffer.wrap(payload);

            byte[] iv = new byte[GCM_IV_LENGTH];
            buffer.get(iv);

            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to decrypt value", ex);
        }
    }
}
