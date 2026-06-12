package com.lifeofpaw.project.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final Path rootLocation;
	
	public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
		this.rootLocation=Paths.get(uploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage directories!", e);
		}
	}
	
	public String storeFile(MultipartFile file, String subFolder) {
		if(file==null || file.isEmpty()) {
			return null;
		}
		
		String contentType=file.getContentType();
		if(contentType==null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("Only image files (.jpg, .jpeg, .png) are allowed!");
		}
		
		try {
            Path targetFolder = this.rootLocation.resolve(subFolder);
            Files.createDirectories(targetFolder);

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null && originalFileName.contains(".") 
                    ? originalFileName.substring(originalFileName.lastIndexOf(".")) 
                    : ".jpg";
            
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            Path targetPath = targetFolder.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subFolder + "/" + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not store file down to disk. Error: " + e.getMessage(), e);
        }
	}
}
