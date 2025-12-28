package me.electronicsboy.titly.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import me.electronicsboy.titly.exceptions.EncryptionException;

@Service
public class CryptoService {
	private static final int AES_KEY_SIZE = 256; // bits
    private static final int GCM_NONCE_LENGTH = 12; // bytes
    private static final int GCM_TAG_LENGTH = 16; // bytes
    
	public Pair<String, String> generateKeyPair() {
		KeyPairGenerator keyPairGen;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException("The RSA algorithm is missing!");
		}
		keyPairGen.initialize(2048); // or 4096
		KeyPair keyPair = keyPairGen.generateKeyPair();
		
		String base64PublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
		String base64PrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
		
		return Pair.of(base64PublicKey, base64PrivateKey);
	}
	
	public String encryptToBase64(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    public byte[] decryptFromBase64(String base64EncryptedData, PrivateKey privateKey) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(base64EncryptedData);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }
    
    public PrivateKey privateKeyFromBase64(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Base64 to PrivateKey", e);
        }
    }
    
    public PublicKey publicKeyFromBase64(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Base64 to PublicKey", e);
        }
    }
    
    public String hmacSHA256(String message, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(keySpec);
        byte[] macBytes = sha256_HMAC.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(macBytes);
    }

	public String base64FromPublicKey(PublicKey publicKey) {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}
	
	/**
     * Hybrid encrypt: returns JSON string with encrypted AES key + encrypted data
     */
    public String hybridEncrypt(byte[] plaintext, PublicKey rsaPublicKey) {
        try {
            // 1. Generate AES key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(AES_KEY_SIZE);
            SecretKey aesKey = keyGen.generateKey();

            // 2. Encrypt plaintext with AES/GCM
            byte[] nonce = new byte[GCM_NONCE_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(nonce);

            Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
            byte[] encryptedData = aesCipher.doFinal(plaintext);

            // 3. Encrypt AES key with RSA public key
            byte[] encryptedAesKey = encryptToBytes(aesKey.getEncoded(), rsaPublicKey);

            // 4. Encode everything base64
            String b64EncryptedAesKey = Base64.getEncoder().encodeToString(encryptedAesKey);
            String b64Nonce = Base64.getEncoder().encodeToString(nonce);
            String b64EncryptedData = Base64.getEncoder().encodeToString(encryptedData);

            // 5. Package JSON (you can use Jackson or manually build JSON)
            // Here's manual construction, you can replace with proper JSON lib
            String json = String.format(
                "{\"key\":\"%s\",\"nonce\":\"%s\",\"data\":\"%s\"}",
                b64EncryptedAesKey, b64Nonce, b64EncryptedData
            );

            return json;

        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    /**
     * Hybrid decrypt: accepts JSON string with base64 encrypted AES key + nonce + encrypted data
     */
    public byte[] hybridDecrypt(String json, PrivateKey rsaPrivateKey) {
        try {
            // Parse JSON (replace with proper JSON lib if needed)
            // Assuming you have Jackson ObjectMapper injected or static instance
            JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);

            String b64EncryptedAesKey = node.get("key").asText();
            String b64Nonce = node.get("nonce").asText();
            String b64EncryptedData = node.get("data").asText();

            byte[] encryptedAesKey = Base64.getDecoder().decode(b64EncryptedAesKey);
            byte[] nonce = Base64.getDecoder().decode(b64Nonce);
            byte[] encryptedData = Base64.getDecoder().decode(b64EncryptedData);

            // 1. Decrypt AES key with RSA private key
            byte[] aesKeyBytes = decryptFromBytes(encryptedAesKey, rsaPrivateKey);
            SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

            // 2. Decrypt data with AES/GCM
            Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, spec);

            return aesCipher.doFinal(encryptedData);

        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    // Helper: encrypt bytes with RSA, return raw bytes (not base64)
    private byte[] encryptToBytes(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    // Helper: decrypt bytes with RSA, input raw bytes
    private byte[] decryptFromBytes(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }
}