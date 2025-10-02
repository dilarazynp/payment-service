package com.venhancer.payment_service.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class SipayHashUtil {

    private static final SecureRandom RNG = new SecureRandom();

    public static String generateHashKey(String total,
                                         String installment,
                                         String currencyCode,
                                         String merchantKey,
                                         String invoiceId,
                                         String appSecret) {
        try {
            // Data string
            String data = String.join("|", total, installment, currencyCode, merchantKey, invoiceId);

            // Generate IV (16 bytes)
            String iv = sha1Hex(String.valueOf(RNG.nextInt()));
            iv = iv.substring(0, 16);
            byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);

            // Password from app secret
            String password = sha1Hex(appSecret);

            // Generate salt (4 characters)
            String salt = sha1Hex(String.valueOf(RNG.nextInt())).substring(0, 4);

            // Salt with password
            String saltWithPassword = sha256Hex(password + salt);

            // Prepare 32-byte key for AES-256
            byte[] keyBytes = saltWithPassword.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length > 32) {
                byte[] tmp = new byte[32];
                System.arraycopy(keyBytes, 0, tmp, 0, 32);
                keyBytes = tmp;
            }

            // Encrypt with AES-256-CBC
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // PHP's openssl_encrypt returns base64 by default - so encode here
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);

            // Bundle: iv:salt:encrypted
            String bundle = iv + ":" + salt + ":" + encrypted;
            System.out.println("Data to hash: " + data);
            System.out.println("Hash key: " + bundle);
            // Replace / with __
            return bundle.replace("/", "__");

        } catch (Exception e) {
            throw new RuntimeException("Hash key generation failed", e);
        }
    }

    private static String sha1Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(digest);
    }

    private static String sha256Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(digest);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}