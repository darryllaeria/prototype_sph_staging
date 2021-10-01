package com.proto.type.base.data.encryption.payload_encryption;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

public final class CryptUtil {

    private static CryptUtil instance;
    private SecretKeyFactory pbeKeyFactory;
    private Cipher aesCipher;
    private Cipher rsaCipher;

    private CryptUtil() {
        try {
            // Use compatibility key factory -- only uses lower 8-bits of passphrase chars
            pbeKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1And8bit");

            aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static CryptUtil getInstance() {
        if (instance == null) {
            instance = new CryptUtil();
        }
        return instance;
    }

    static SecretKey generateSecretKey() {

        SecretKeyFactory pbeKeyFactory = getInstance().pbeKeyFactory;
        SecretKey secretKey;
        try {
            SecureRandom rand = new SecureRandom();
            byte[] salt = new byte[32];
            rand.nextBytes(salt);
            // WORK-AROUND change rand.nextInt(2048) to rand.nextInt(2047) + 1 to genterate 1-2048;
            int iterationCount = rand.nextInt(2047) + 1;
            KeySpec pbeKeySpec = new PBEKeySpec(new String(rand.generateSeed(24)).toCharArray(), salt, iterationCount, 256);
            SecretKey pbeKey = pbeKeyFactory.generateSecret(pbeKeySpec);
            secretKey = new SecretKeySpec(pbeKey.getEncoded(), "AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return secretKey;
    }

    static SymmetricEncryptionEntry symmetricEncrypt(SecretKey key, String data) {

        Cipher cipher = getInstance().aesCipher;
        SymmetricEncryptionEntry entry;
        try {
            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            entry = new SymmetricEncryptionEntry(cipherText, iv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return entry;
    }

    private static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        new SecureRandom().nextBytes(b);

        return b;
    }

    public static byte[] asymmetricKeyEncrypt(PublicKey key, byte[] prefixData) {
        Cipher cipher = getInstance().rsaCipher;
        byte[] cipherData = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new OAEPParameterSpec("SHA-256",
                    "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
            cipherData = cipher.doFinal(prefixData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherData;
    }

    public static String symmetricDataDecrypt(byte[] aesDeviceKey, byte[] encryptedData, byte[] aesIV) {
        SecretKey aesSecretKey = new SecretKeySpec(aesDeviceKey, "AES");
        String plainText;
        try {
            Cipher cipher = getInstance().aesCipher;
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(aesIV);
            cipher.init(Cipher.DECRYPT_MODE, aesSecretKey, paramSpec);
            byte [] decryptedData = cipher.doFinal(encryptedData);
            plainText = new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return plainText;
    }

    public static class SymmetricEncryptionEntry {
        private final byte[] cipherData;
        private final byte[] iV;

        SymmetricEncryptionEntry(byte[] cipherData, byte[] iV) {
            this.cipherData = cipherData;
            this.iV = iV;
        }

        public byte[] getCipherData() {
            return cipherData;
        }

        public byte[] getIv() {
            return iV;
        }
    }
}
