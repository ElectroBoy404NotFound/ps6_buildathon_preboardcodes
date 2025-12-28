package me.electronicsboy.titly.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.electronicsboy.titly.data.FileType;
import me.electronicsboy.titly.exceptions.FileNotFoundException;
import me.electronicsboy.titly.exceptions.InvalidFileException;
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
    
    @GetMapping("/getFile/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable long id) throws MalformedURLException {
    	FileObject object = fileObjectRepository.findById(id).orElseThrow();
    	Path filePath = Paths.get(object.getFilepath()).normalize();
    	Resource resource = new UrlResource(filePath.toUri());

        // Return file as attachment
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @GetMapping("/video/{id}/{filename}")
    public ResponseEntity<Resource> streamVideo(@PathVariable long id, @PathVariable String filename, @RequestHeader HttpHeaders headers) {
        FileObject fileObject = fileObjectRepository.findById(id).orElseThrow();
        
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

        // Determine content type
        String contentType;
		try {
			contentType = Files.probeContentType(path);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(compatibleFile.length())
                .body(resource);
    }
}
