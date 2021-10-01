package com.proto.type.base.data.encryption.payload_encryption;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.SecretKey;

public class AesRsaCipher {
    private static AesRsaCipher aesRsaCipher;

    public final SecretKey mAesKey;
    public CryptUtil.SymmetricEncryptionEntry aesEntry;
    private PublicKey mServerPublicKey;

    private AesRsaCipher() {
        mAesKey = CryptUtil.generateSecretKey();
        // String mServerPubKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6+EzlBu2jFp8Krf9R4M055HzblmUyEuScq/CZ3GRITf3mrgY+6mstaqPpHiJIyUXVCeal58e4nsEjAsjqr3CNXo/YiNbLhxrlWgeH8HDIpjYXXLUbN1XMk4qdl3lCHvLtQ8DjEyNsBRrG6Jwg9fnxxA+EHfx18ysqS8PWJ8UhPgVyY/0A+SL56Gj8CbhOYo8a30VMdlQJOIm1YBw7LT7Efo6v4O+S4N0otSOUJCyO8KwO3qy050SoxskyMcW7MDqimFbBDB9ZiG+vl12Fiff1qRMHkRnDPMI3rK9zyt3LSrrIeHRcKV6dcExl3wWcwHaEWZsRkfpNotM+nIj+X4t1QIDAQAB";
       /* String mServerPubKeyStr = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6+EzlBu2jFp8Krf9R4M0\n" +
                "55HzblmUyEuScq/CZ3GRITf3mrgY+6mstaqPpHiJIyUXVCeal58e4nsEjAsjqr3C\n" +
                "NXo/YiNbLhxrlWgeH8HDIpjYXXLUbN1XMk4qdl3lCHvLtQ8DjEyNsBRrG6Jwg9fn\n" +
                "xxA+EHfx18ysqS8PWJ8UhPgVyY/0A+SL56Gj8CbhOYo8a30VMdlQJOIm1YBw7LT7\n" +
                "Efo6v4O+S4N0otSOUJCyO8KwO3qy050SoxskyMcW7MDqimFbBDB9ZiG+vl12Fiff\n" +
                "1qRMHkRnDPMI3rK9zyt3LSrrIeHRcKV6dcExl3wWcwHaEWZsRkfpNotM+nIj+X4t\n" +
                "1QIDAQAB\n" +
                "-----END PUBLIC KEY-----";
*/
        String mServerPubKeyStr = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp4uiNCd0B5xuHDlehUfo\n" +
                "ugjV5LEaTwO9lqPbxeyuSL6jMCSBaSCvQAYNHkw2S8Iw3xAIQzLkgzOMVuVGDvNi\n" +
                "3eaKqeNajrZKXwUmIamt36FeOH8Uv6en4bJhWIWo9Lse816/4nkswJ+Yb25Xgr0+\n" +
                "pVt//ol75lFbRfPc2d55ZFFKiwu5wnuQcbpnb9TlP07S8W4DxyfGqM4TlU1umRBY\n" +
                "cVXww2qDf0Y3wuizf5XIp1ladIVvHVaGn1tknVDEKn+Uld3BRrWpvRuCXz326RTS\n" +
                "2Kdd680oGqT1aHI611fdtzqehENd4LyJjJGwWNi2mpT2Pk+ZI5gteMlyFHcnbmJW\n" +
                "GQIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        String publicKeyContent = mServerPubKeyStr.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");

        KeyFactory rsaKeyFactory;
        try {
            rsaKeyFactory = KeyFactory.getInstance("RSA");
            mServerPublicKey = rsaKeyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyContent)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public static AesRsaCipher getInstance() {
        if (aesRsaCipher == null) {
            return aesRsaCipher = new AesRsaCipher();
        }
        return aesRsaCipher;
    }

    public CryptUtil.SymmetricEncryptionEntry encryptRequest(String dataStr) {
        return CryptUtil.symmetricEncrypt(mAesKey, dataStr);
    }

    private byte[] getMd5(String text) {
        byte[] messageDigest = "".getBytes();
        try {
            byte[] bytesOfMessage = text.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            messageDigest = md.digest(bytesOfMessage);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return messageDigest;
    }

    public byte[] getEncryptedPrefixData(String data) {
        aesEntry = encryptRequest(data);
        byte[] combinedData = concat(mAesKey.getEncoded(), aesEntry.getIv());
        System.out.println("Secret AES Key ->" + Base64.encodeBytes(mAesKey.getEncoded()));
        System.out.println("IV ->" + Base64.encodeBytes(aesEntry.getIv()));
        byte[] encryptedPrefixData = concat(combinedData, getMd5(data));
        System.out.println("md5 of message ->" + Base64.encodeBytes(getMd5(data)));
        /*String plainText = CryptUtil.symmetricDataDecrypt(mAesKey.getEncoded(), aesEntry.getCipherData(), aesEntry.getIv());
        System.out.println("Plain Text is - > " + plainText);
        if (Arrays.equals(getMd5(dataModule), getMd5(plainText))){
            System.out.println("md5 is okay - > ");
        }*/
        return CryptUtil.asymmetricKeyEncrypt(mServerPublicKey, encryptedPrefixData);
    }

    public String symmetricDataDecrypt(byte[] aesKeyStr, byte[] data, byte[] iV) {
        return CryptUtil.symmetricDataDecrypt(aesKeyStr, data, iV);
    }

    private byte[] concat(byte[] a, byte[] b) {
        int lenA = a.length;
        int lenB = b.length;
        byte[] c = Arrays.copyOf(a, lenA + lenB);
        System.arraycopy(b, 0, c, lenA, lenB);
        return c;
    }
}
