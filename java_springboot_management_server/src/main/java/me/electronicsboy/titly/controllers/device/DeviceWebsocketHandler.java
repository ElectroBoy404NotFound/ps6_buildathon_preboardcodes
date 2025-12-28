package me.electronicsboy.titly.controllers.device;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.electronicsboy.titly.data.DeviceAuthenticationStatus;
import me.electronicsboy.titly.data.DeviceRequestType;
import me.electronicsboy.titly.dtos.ws.DeviceMessageDto;
import me.electronicsboy.titly.exceptions.InvalidRequestException;
import me.electronicsboy.titly.temporary.objects.TemporaryDeviceWebsocketSession;
import me.electronicsboy.titly.temporary.repositories.TemporaryDeviceObjectRepository;
import me.electronicsboy.titly.temporary.repositories.TemporaryDeviceWebsocketSessionRepository;

@Component
public class DeviceWebsocketHandler extends AbstractWebSocketHandler {
	private TemporaryDeviceWebsocketSessionRepository sessionRepository;
	private TemporaryDeviceObjectRepository objectRepository;
	private DeviceWebsocketPreauthHandler preauthHandler;
	private DeviceWebsocketGeneralHandler generalHandler;
	
	public DeviceWebsocketHandler(TemporaryDeviceWebsocketSessionRepository sessionRepository, TemporaryDeviceObjectRepository objectRepository, DeviceWebsocketPreauthHandler preauthHandler, DeviceWebsocketGeneralHandler generalHandler) {
		this.sessionRepository = sessionRepository;
		this.objectRepository = objectRepository;
		this.preauthHandler = preauthHandler;
		this.generalHandler = generalHandler;
	}

	private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
	
	@Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Client connected: " + session.getId());
        TemporaryDeviceWebsocketSession sessionEntity = new TemporaryDeviceWebsocketSession(null, session.getId(), DeviceAuthenticationStatus.PRE_AUTH_START, null, null, true, null); 
        sessionRepository.save(sessionEntity);
        session.sendMessage(new TextMessage("{\"type\":\"welcome\",\"msg\":\"Connected\"}"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession wssession, TextMessage message) throws IOException {
    	TemporaryDeviceWebsocketSession deviceSession = sessionRepository.findBySessionId(wssession.getId()).orElseThrow();
    	String payload = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        DeviceMessageDto deviceRequest = mapper.readValue(payload, DeviceMessageDto.class);

//        if(deviceSession.getAuthenticationStatus() == DeviceAuthenticationStatus.PRE_AUTH_START && (deviceRequest.getRequestType() != DeviceRequestType.HANDSHAKE && deviceRequest.getRequestType() != DeviceRequestType.CRYPTO_SHARE))
//        	throw new InvalidRequestException("You must first finish the previous ones!");
        if((deviceRequest.getRequestType() == DeviceRequestType.HANDSHAKE || deviceRequest.getRequestType() == DeviceRequestType.CRYPTO_SHARE) && deviceSession.getAuthenticationStatus() != DeviceAuthenticationStatus.PRE_AUTH_START)
        	throw new InvalidRequestException("You can't run these again!");
        
        ObjectNode jsonbody;
        
        payload = deviceRequest.getData();
        
        switch(deviceRequest.getRequestType()) {
        case HANDSHAKE:
        	jsonbody = preauthHandler.handleHandshake(payload);
        	break;
        case CRYPTO_SHARE:
        	jsonbody = preauthHandler.handleCryptoShare(deviceSession, payload);
        	break;
        case AUTH_TEST:
        	jsonbody = preauthHandler.handleAuthTest(deviceSession, payload);
        	break;
        case GENERAL_HANDLER:
        	jsonbody = generalHandler.handle(deviceSession, payload);
        	break;
        default:
        	jsonbody = mapper.createObjectNode();

        	jsonbody.put("message", "Invalid request type");
        	jsonbody.put("status", "error");
        	break;
        }
       
        jsonbody.putPOJO("requestType", deviceRequest.getRequestType());
        jsonbody.put("session_id", wssession.getId());
        jsonbody.put("timestamp", getCurrentTimestamp());
        
        String jsonString = mapper.writeValueAsString(jsonbody);
        wssession.sendMessage(new TextMessage(jsonString));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        ByteBuffer buffer = message.getPayload();

        session.sendMessage(new BinaryMessage(buffer));
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession wssession, CloseStatus status) throws Exception {
    	 TemporaryDeviceWebsocketSession deviceSession = sessionRepository.findBySessionId(wssession.getId()).orElseThrow();
    	 objectRepository.deleteById(deviceSession.getDeviceObject());
    	 sessionRepository.deleteBySessionId(deviceSession.getSessionId());
    }
}
