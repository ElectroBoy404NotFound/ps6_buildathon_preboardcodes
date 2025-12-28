package me.electronicsboy.titly.controllers.device;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.electronicsboy.titly.crypto.CryptoService;
import me.electronicsboy.titly.crypto.KeyStoreService;
import me.electronicsboy.titly.data.DeviceAuthenticationStatus;
import me.electronicsboy.titly.data.FileType;
import me.electronicsboy.titly.dtos.ws.DeviceMessageDataDto;
import me.electronicsboy.titly.exceptions.FileNotFoundException;
import me.electronicsboy.titly.exceptions.InvalidFileException;
import me.electronicsboy.titly.exceptions.InvalidRequestException;
import me.electronicsboy.titly.models.FileObject;
import me.electronicsboy.titly.repositories.FileObjectRepository;
import me.electronicsboy.titly.responses.SafeFileResponse;
import me.electronicsboy.titly.services.FileStorageService;
import me.electronicsboy.titly.temporary.objects.TemporaryDeviceWebsocketSession;

@Service
public class DeviceWebsocketGeneralHandler {
	@Autowired
	private CryptoService cryptoService;
	@Autowired
	private KeyStoreService keyStoreService;
	@Autowired
	private FileObjectRepository fileObjectRepository;
	@Autowired
	private FileStorageService fileService;
	
	private ObjectMapper mapper;
	
	public DeviceWebsocketGeneralHandler() {
		mapper = new ObjectMapper();
	}

	private ObjectNode doMediaList() {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode array = root.putArray("files");
        
    	List<FileObject> fileObjects = fileObjectRepository.findAll();
    	for(FileObject file : fileObjects) {
    		array.addPOJO(SafeFileResponse.fromFileObject(file));
    	}
        
        return root;
	}
	
	private ObjectNode doVideoStream(String payload) throws JsonMappingException, JsonProcessingException {
		ObjectNode payloadData = (ObjectNode) mapper.readTree(payload);
		long fileid = payloadData.path("fileid").asLong();
		String filename = payloadData.path("filename").asText();
		
		ObjectNode node = mapper.createObjectNode();
		FileObject fileObject = fileObjectRepository.findById(fileid).orElseThrow();
        
        if(fileObject.getFiletype() != FileType.VIDEO)
        	throw new InvalidFileException("Only video files can be streamed!");

        fileService.convertFile(fileObject);

        File compatibleFile = fileService.getConvertedVideoFile(fileObject, filename);
        
        // Return as a streaming resource
        Path path = compatibleFile.toPath();
        Resource resource;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

        if (!resource.exists()) {
            throw new FileNotFoundException("The requested file %s could not be found on the server!".formatted(filename));
        }
        
        node.put("filename", filename);
        try {
			node.put("filedata", fileService.resourceToBase64(resource));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        
        return node;
	}
	
	private ObjectNode doGetFile(String payload) throws JsonMappingException, JsonProcessingException {
		ObjectNode payloadData = (ObjectNode) mapper.readTree(payload);
		long fileid = payloadData.path("fileid").asLong();
		
		ObjectNode node = mapper.createObjectNode();
		FileObject fileObject = fileObjectRepository.findById(fileid).orElseThrow();
		
		File compatibleFile = new File(fileObject.getFilepath());
        
        // Return as a streaming resource
        Path path = compatibleFile.toPath();
        Resource resource;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

        if (!resource.exists()) {
            throw new FileNotFoundException("The requested file %s could not be found on the server!".formatted(fileid));
        }
        
        node.put("fileid", fileid);
        try {
			node.put("filedata", fileService.resourceToBase64(resource));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        
        return node;
	}
	
	public ObjectNode handle(TemporaryDeviceWebsocketSession session, String payload) throws JsonMappingException, JsonProcessingException {
		if(session.getAuthenticationStatus() != DeviceAuthenticationStatus.CRYPTO_SHARED)
			throw new InvalidRequestException("You can't run these requests yet!");

        ObjectNode root = mapper.createObjectNode();
        
        String decryptedData = new String(cryptoService.hybridDecrypt(payload, keyStoreService.getPrivateKey()), StandardCharsets.UTF_8);
        DeviceMessageDataDto deviceRequest;
        try {
			deviceRequest = mapper.readValue(decryptedData, DeviceMessageDataDto.class);
		} catch (JsonProcessingException e) {
			throw new InvalidRequestException(e.getMessage());
		}
        
        ObjectNode response = null;
        switch(deviceRequest.getRequestType()) {
        case MEDIA_LIST:
        	response = doMediaList();
        	break;
        case VIDEO_FILE_DATA_REQUEST:
        	response = doVideoStream(deviceRequest.getData());
        	break;
        case OTHER_FILE_DATA_REQUEST:
        	response = doGetFile(deviceRequest.getData());
        	break;
        default:
        	response = mapper.createObjectNode();

        	response.put("message", "Invalid request type");
        	response.put("status", "error");
        }
        String responseJsonString;
		try {
			responseJsonString = mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        System.out.println(responseJsonString);
        String encodedDataString = cryptoService.hybridEncrypt(responseJsonString.getBytes(), session.getDevicePublicKey());
        root.put("data", encodedDataString);
        
        return root;
	}
}
