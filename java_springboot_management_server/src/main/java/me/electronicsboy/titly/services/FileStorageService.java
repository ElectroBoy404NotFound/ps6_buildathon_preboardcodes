package me.electronicsboy.titly.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import me.electronicsboy.titly.data.FileType;
import me.electronicsboy.titly.exceptions.FileErrorException;
import me.electronicsboy.titly.exceptions.InvalidFileNameException;
import me.electronicsboy.titly.helpers.VideoFragmenter;
import me.electronicsboy.titly.models.FileObject;
import me.electronicsboy.titly.repositories.FileObjectRepository;

@Service
public class FileStorageService {

	@Value("${file.storage.uploads.location}")
    private String storageUploadsLocation;
	@Value("${file.storage.conversion.location}")
    private String storageConversionLocation;

    private final FileObjectRepository fileRepo;

    private final String videoFileNameRegex = "^output(?:\\.m3u8|(?:0|[1-9][0-9]{0,2})\\.ts)$";
    private final Pattern videoFilePattern;
    
    public FileStorageService(FileObjectRepository fileRepo) {
        this.fileRepo = fileRepo;
        this.videoFilePattern = Pattern.compile(videoFileNameRegex);
    }

    public FileObject storeFile(MultipartFile file, String givenfilename, FileType fileType, String grade, String subject, String term, String teacher) {
    	String originalFilename = file.getOriginalFilename();
    	String extension = "";

    	int dotIndex = originalFilename.lastIndexOf('.');
    	if (dotIndex != -1) {
    	    extension = originalFilename.substring(dotIndex);
    	    originalFilename = givenfilename;
    	}

    	String filename = originalFilename + "-" + teacher + "-" + System.currentTimeMillis() + extension;
    	Path directoryPath = Path.of((storageUploadsLocation + "/" + grade + "/" + subject + "/" + term).replaceAll(" ", "_"));

    	// Create the directories if they don't exist
    	try {
			Files.createDirectories(directoryPath);
		} catch (IOException e) {
			throw new FileErrorException("An error occured while creating directories: " + e.getMessage());
		}  // <-- This line ensures all folders exist

    	// Resolve the final file path
    	Path targetLocation = directoryPath.resolve(filename.replaceAll(" ", "_"));
    	
        try {
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FileErrorException("An error occured while downloading the file: " + e.getMessage());
		}

        FileObject fileObject = new FileObject(
            givenfilename,
            fileType, 
            grade,
            subject,
            term,
            teacher,
            targetLocation.toAbsolutePath().toString(),
            false
        );

        return fileRepo.save(fileObject);
    }
    
    public void deleteFile(FileObject fileObject) {
    	File physicalFile = new File(fileObject.getFilepath());
    	if(physicalFile.exists())
    		physicalFile.delete();
    	
    	if (!fileObject.isConverted()) {
    		Path conversionFolder = Path.of((storageConversionLocation + "/" + fileObject.getId() + "_" + fileObject.getFilename()).replaceAll(" ", "_"));
    		File conversionFolderFile = conversionFolder.toFile();
    		if(conversionFolderFile.exists())
    			conversionFolderFile.delete();
    	}
    	
    	fileRepo.delete(fileObject);
    }
    
    public void convertFile(FileObject fileObject) {
    	if (!fileObject.isConverted()) {
    		File originalFile = new File(fileObject.getFilepath());
    		
    		System.out.println(storageConversionLocation);
            VideoFragmenter.convertToHLS(originalFile.getAbsolutePath(), (storageConversionLocation + "/" + fileObject.getId() + "_" + fileObject.getFilename()).replaceAll(" ", "_"));
            
            fileObject.setConverted(true);
            fileRepo.save(fileObject);
        }
    }
    
    public File getConvertedVideoFile(FileObject fileObject, String filename) {
    	Matcher matcher = videoFilePattern.matcher(filename);
    	if(!matcher.matches())
    		throw new InvalidFileNameException("%s is an invalid filename for video files! Allowed are 'output.m3u8' and 'outputx.ts' where x can be any natural number from 0 to 999, both inclusive");
    	
    	String compatiblePath = (storageConversionLocation + "/" + fileObject.getId() + "_" + fileObject.getFilename() + "/" + filename).replaceAll(" ", "_");
        File compatibleFile = new File(compatiblePath);
        
        return compatibleFile;
    }
    
    public String resourceToBase64(Resource resource) throws Exception {
        try (InputStream inputStream = resource.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
             
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            byte[] fileBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }
}
