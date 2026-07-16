package com.lifeofpaw.project.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class FileStorageService {

	@Autowired
	private Cloudinary cloudinary;

	public String storeFile(MultipartFile file, String folderName) {
		if (file == null || file.isEmpty()) {
			return null;
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("Only image files (.jpg, .jpeg, .png) are allowed!");
		}

		try {
			Map<?, ?> uploadResult = cloudinary.uploader().upload(
				file.getBytes(),
				ObjectUtils.asMap("folder", "lifeofpaw/" + folderName)
			);
			return (String) uploadResult.get("secure_url");
		} catch (IOException e) {
			throw new RuntimeException("Could not upload file to Cloudinary. Error: " + e.getMessage(), e);
		}
	}
}
