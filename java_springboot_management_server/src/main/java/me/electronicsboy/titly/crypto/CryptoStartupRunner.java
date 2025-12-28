package me.electronicsboy.titly.crypto;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class CryptoStartupRunner implements CommandLineRunner {
	private final KeyStoreService keyStoreService;
	private final CryptoService cryptoService;

    public CryptoStartupRunner(KeyStoreService keyStoreService, CryptoService cryptoService) {
        this.keyStoreService = keyStoreService;
        this.cryptoService = cryptoService;
    }
	
	@Override
	public void run(String... args) throws Exception {
		Pair<String, String> cryptoKeys = cryptoService.generateKeyPair();
        keyStoreService.setKeys(cryptoService.publicKeyFromBase64(cryptoKeys.getFirst()), cryptoService.privateKeyFromBase64(cryptoKeys.getSecond()));
	}
}
