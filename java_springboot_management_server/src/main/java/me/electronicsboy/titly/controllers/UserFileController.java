package me.electronicsboy.titly.controllers;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.electronicsboy.titly.models.FileObject;
import me.electronicsboy.titly.models.User;
import me.electronicsboy.titly.repositories.FileObjectRepository;

@RequestMapping("/files")
@RestController
public class UserFileController {
	private final FileObjectRepository fileObjectRepository;
    
    public UserFileController(FileObjectRepository fileObjectRepository) {
		this.fileObjectRepository = fileObjectRepository;
	}
    
    @GetMapping("/listFiles")
    public ResponseEntity<List<FileObject>> listFile() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		
    	List<FileObject> fileObjects = fileObjectRepository.findByUser(currentUser).orElseThrow();
    	return ResponseEntity.ok(fileObjects);
    }
    
//    @GetMapping("/getFile/{id}")
//    public ResponseEntity<Resource> getFile(@PathVariable long id) throws MalformedURLException {
//    	FileObject object = fileObjectRepository.findById(id).orElseThrow();
//    	Path filePath = Paths.get(object.getFilepath()).normalize();
//    	Resource resource = new UrlResource(filePath.toUri());
//
//        // Return file as attachment
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//    }
//    
//    @PostMapping("/upload")
//    public ResponseEntity<FileObject> uploadFile(
//        @RequestParam("file") MultipartFile file,
//        @RequestParam String filename
//    ) {
//    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		User currentUser = (User) authentication.getPrincipal();
//		if(currentUser.getPrivilegeLevel().compareTo(PrivilegeLevel.ADMIN) <= 0)
//			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
//		
//        try {
//            FileObject stored = fileService.storeFile(file, filename, fileType, grade, subject, term, teacher);
//            return ResponseEntity.ok(SafeFileResponse.fromFileObject(stored));
//        } catch (Exception e) {
//        	throw new RuntimeException(e);
//        }
//    }
    
    // Shameless copy-paste from ChatGPT
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String filename
    ) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	User currentUser = (User) authentication.getPrincipal();
        try {
            // 1. Create headers for multipart/form-data
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 2. Wrap the file into a Resource
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename(); // Important, Python backend expects 'filename'
                }
            };

            // 3. Build the multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileAsResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 4. Send POST request to Python backend
            RestTemplate restTemplate = new RestTemplate();
            String pythonUrl = "http://192.168.1.14:5000/upload_file"; // adjust port if needed
            ResponseEntity<String> response = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.getBody());
            
            String rbody = response.getBody();
            System.out.println(rbody);
            
            fileObjectRepository.save(new FileObject(filename, currentUser, jsonNode.get("digest").asText()));
            
            // 5. Forward Python response
            return ResponseEntity.status(response.getStatusCode()).body(rbody);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"Something went wrong!\"}");
        }
    }

}
