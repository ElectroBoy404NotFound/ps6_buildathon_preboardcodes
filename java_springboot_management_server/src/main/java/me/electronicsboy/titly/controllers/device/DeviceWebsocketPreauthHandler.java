package me.electronicsboy.titly.controllers.device;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.electronicsboy.titly.crypto.CryptoService;
import me.electronicsboy.titly.crypto.KeyStoreService;
import me.electronicsboy.titly.data.DeviceAuthenticationStatus;
import me.electronicsboy.titly.temporary.objects.TemporaryDeviceObject;
import me.electronicsboy.titly.temporary.objects.TemporaryDeviceWebsocketSession;
import me.electronicsboy.titly.temporary.repositories.TemporaryDeviceObjectRepository;
import me.electronicsboy.titly.temporary.repositories.TemporaryDeviceWebsocketSessionRepository;

@Service
public class DeviceWebsocketPreauthHandler {
	@Autowired
	private TemporaryDeviceWebsocketSessionRepository websocketSessionRepo;
	@Autowired
	private TemporaryDeviceObjectRepository temporaryDeviceObjectRepository;
	@Autowired
	private CryptoService cryptoService;
	@Autowired
	private KeyStoreService keyStoreService;
	
	public ObjectNode handleHandshake(String payload) {
		ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

		root.put("payload", payload);
		root.put("status", "ack");
		
		return root;
	}
	
	public ObjectNode handleCryptoShare(TemporaryDeviceWebsocketSession session, String payload) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        
        ObjectNode payloadData = (ObjectNode) mapper.readTree(payload);
        session.setDevicePublicKey(cryptoService.publicKeyFromBase64(payloadData.path("public_key").asText()));
        
        TemporaryDeviceObject object = new TemporaryDeviceObject(null, payloadData.path("device_id").asText(), payloadData.path("device_name").asText());
        temporaryDeviceObjectRepository.save(object);
        
        root.put("publickey", cryptoService.base64FromPublicKey(keyStoreService.getPublicKey()));
        
        session.setAuthenticationStatus(DeviceAuthenticationStatus.CRYPTO_SHARED);
        session.setDeviceObject(object);
        websocketSessionRepo.save(session);
        
        return root;
	}

	public ObjectNode handleAuthTest(TemporaryDeviceWebsocketSession session, String payload) {
		ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        
        String jsonForHybridDecrypt = payload;
        
        String sentByClient = new String(cryptoService.hybridDecrypt(jsonForHybridDecrypt, keyStoreService.getPrivateKey()), StandardCharsets.UTF_8);
        root.put("reencryption", cryptoService.hybridEncrypt(sentByClient.getBytes(), session.getDevicePublicKey()));
        
        return root;
	}
}
