package com.lifeofpaw.project.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

	@Value("${file.upload-dir}")
    private String uploadDir;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		
		Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String nativeFolderLocation = uploadPath.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(nativeFolderLocation);
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		
		.allowedOrigins("http://localhost:5173") 
        // Allows all common HTTP methods
        .allowedMethods("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS")
        // Allows all headers (like Authorization headers carrying your JWT tokens)
        .allowedHeaders("*")
        // Allows cookies or authentication headers to pass through safely
        .allowCredentials(true);
	}
	
}
