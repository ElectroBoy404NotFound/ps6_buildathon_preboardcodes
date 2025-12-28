package me.electronicsboy.titly.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.stereotype.Component;

@Component
public class KeyStoreService {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public void setKeys(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
