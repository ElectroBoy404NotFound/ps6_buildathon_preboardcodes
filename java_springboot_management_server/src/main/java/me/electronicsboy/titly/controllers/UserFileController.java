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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.electronicsboy.titly.data.FileType;
import me.electronicsboy.titly.data.PrivilegeLevel;
import me.electronicsboy.titly.exceptions.FileNotFoundException;
import me.electronicsboy.titly.exceptions.InvalidFileException;
import me.electronicsboy.titly.exceptions.UnprivilagedExpection;
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
//        @RequestParam String grade,
//        @RequestParam String subject,
//        @RequestParam String filename,
//        @RequestParam(required = false) String term,
//        @RequestParam(required = false) String teacher,
//        @RequestParam FileType fileType
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
}
