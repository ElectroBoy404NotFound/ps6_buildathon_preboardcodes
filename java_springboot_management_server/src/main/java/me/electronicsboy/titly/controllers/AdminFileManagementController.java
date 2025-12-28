package me.electronicsboy.titly.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.electronicsboy.titly.data.FileType;
import me.electronicsboy.titly.data.PrivilegeLevel;
import me.electronicsboy.titly.exceptions.UnprivilagedExpection;
import me.electronicsboy.titly.models.FileObject;
import me.electronicsboy.titly.models.User;
import me.electronicsboy.titly.repositories.FileObjectRepository;
import me.electronicsboy.titly.responses.OkResponse;

@RequestMapping("/admin/files")
@RestController
public class AdminFileManagementController {
	private final FileObjectRepository fileObjectRepository;
    
    public AdminFileManagementController(FileObjectRepository fileObjectRepository) {
		this.fileObjectRepository = fileObjectRepository;
	}
    
    @PostMapping("/upload")
    public ResponseEntity<FileObject> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam String grade,
        @RequestParam String subject,
        @RequestParam String filename,
        @RequestParam(required = false) String term,
        @RequestParam(required = false) String teacher,
        @RequestParam FileType fileType
    ) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		if(currentUser.getPrivilegeLevel().compareTo(PrivilegeLevel.ADMIN) <= 0)
			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
		
        try {
            FileObject stored = fileService.storeFile(file, filename, fileType, grade, subject, term, teacher);
            return ResponseEntity.ok(SafeFileResponse.fromFileObject(stored));
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }
}
