package com.lifeofpaw.project.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeofpaw.project.entity.Animal;
import com.lifeofpaw.project.entity.AnimalImage;
import com.lifeofpaw.project.entity.Organizations;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.AnimalImageRepository;
import com.lifeofpaw.project.repository.AnimalRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.repository.UserRepository;
import com.lifeofpaw.project.service.AnimalService;
import com.lifeofpaw.project.service.FileStorageService;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

	@Autowired
	private AnimalService animalService;
	@Autowired
	private AnimalRepository animalRepository;
	@Autowired
	private AnimalImageRepository animalImageRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrganizationRepository organizationRepository;
	@Autowired
	private FileStorageService fileStorageService;
	
	
	
	@PostMapping("/add")
	public Animal add(@jakarta.validation.Valid @RequestBody Animal animal, @RequestParam Long orgId, Principal principal) {
		return animalService.addAnimal(animal,orgId,principal.getName());
	}
		
	@GetMapping("/available")
	public List<Animal> getAvailable(){
		return animalService.getAllAvailableAnimals();
	}
	
	@GetMapping("/search")
	public List<Animal> search(
			@RequestParam String species,
			@RequestParam String gender,
			@RequestParam (required = false)String status
			){
		return animalService.searchAnimal(species, gender, status);
	}
	
	@PutMapping("/{id}")
	public Animal updateAnimal(
			@jakarta.validation.Valid @PathVariable Long id, 
			@RequestBody Animal animalDetails,
			Principal principal){
		
		Animal existing=animalRepository.findById(id)
				.orElseThrow(()->new RuntimeException("Animal not found"));
		
		User user=userRepository.findByEmail(principal.getName()).get();
		
		if (!"admin".equalsIgnoreCase(user.getRole())) {
	        if (existing.getOrganization() == null || 
	            existing.getOrganization().getContactPerson() == null || 
	            existing.getOrganization().getContactPerson().getUserId() != user.getUserId()) {
	            throw new RuntimeException("SECURITY VIOLATION: You do not own this animal listing!");
	        }
	    }
		
		return animalService.updateAnimal(id, animalDetails);
	}
	
	@DeleteMapping("/{id}")
	public String deleteAnimal(@PathVariable Long id, Principal principal) {
		
		Animal existing = animalRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Animal not found"));
	            
	    User user = userRepository.findByEmail(principal.getName()).get();
	    if (!"admin".equalsIgnoreCase(user.getRole())) {
	        if (existing.getOrganization() == null || 
	            existing.getOrganization().getContactPerson() == null || 
	            existing.getOrganization().getContactPerson().getUserId() != user.getUserId()) {
	            throw new RuntimeException("SECURITY VIOLATION: Unauthorized deletion attempt blocked.");
	        }
	    }
		
		
		animalService.deleteAnimal(id);
		return "Animal with ID " + id + " has been deleted successfully.";
	}
	
	@GetMapping("/all")
	public List<Animal> getAll(){
		return animalService.getAllAnimals();
	}
	
	@PostMapping("/{animalId}/add-image")
	public AnimalImage addImage(@PathVariable Long animalId, @RequestParam("file") org.springframework.web.multipart.MultipartFile file, Principal principal) {
		
		Animal animal=animalRepository.findById(animalId)
				.orElseThrow(()->new RuntimeException("Animal not found"));
		
		User user=userRepository.findByEmail(principal.getName()).get();
		
		if (!user.getRole().equalsIgnoreCase("admin")) {
	        Organizations userOrg = organizationRepository.findAll().stream()
	            .filter(o -> o.getContactPerson() != null && o.getContactPerson().getUserId() == user.getUserId())
	            .findFirst()		
	            .orElseThrow(() -> new RuntimeException("You don't own an organization or your NGO isn't linked to your profile!"));

	        if (!animal.getOrganization().getOrgId().equals(userOrg.getOrgId())) {
	            throw new RuntimeException("SECURITY ALERT: You can only upload images for YOUR animals!");
	        }
	    }
		
		if (animal.getImages() != null && animal.getImages().size() >= 1) {
			throw new IllegalArgumentException("LIMIT EXCEEDED: This animal profile already has a cover image. Only 1 image is allowed per animal listing!");
		}
		
		String generatedUrlPath = fileStorageService.storeFile(file, "animals");
		if(generatedUrlPath==null) {
			throw new IllegalArgumentException("Please provide a valid image file to upload.");
		}
		
		AnimalImage img = new AnimalImage();
	    img.setAnimal(animal);
	    img.setImageUrl(generatedUrlPath);	
	    return animalImageRepository.save(img);
		
	}
	
	@GetMapping("/public/org/{orgId}")
	public List<Animal> getPublicAnimalsByOrganization(@PathVariable Long orgId) {
		return animalRepository.findAllByOrganization_OrgId(orgId);
	}
	
	@GetMapping("/get-particular/{animalId}")
	public Animal getParticularAnimal(@PathVariable Long animalId) {
		return animalRepository.findById(animalId)
				.orElseThrow(() -> new RuntimeException("ERROR: Animal profile with ID " + animalId + " does not exist in our registry."));
	}
	
	@DeleteMapping("/{animalId}/remove-image")
	public org.springframework.http.ResponseEntity<?> removeAnimalImage(@PathVariable Long animalId, Principal principal) {
	    
	    Animal animal = animalRepository.findById(animalId)
	            .orElseThrow(() -> new RuntimeException("Animal profile not found"));
	    
	    User user = userRepository.findByEmail(principal.getName()).get();
	    
	    if (!user.getRole().equalsIgnoreCase("admin")) {
	        Organizations userOrg = organizationRepository.findAll().stream()
	            .filter(o -> o.getContactPerson() != null && o.getContactPerson().getUserId() == user.getUserId())
	            .findFirst()        
	            .orElseThrow(() -> new RuntimeException("Security Error: Linked NGO profile not found."));

	        if (!animal.getOrganization().getOrgId().equals(userOrg.getOrgId())) {
	            throw new RuntimeException("SECURITY VIOLATION: You can only remove images for your own animals!");
	        }
	    }
	    
	    if (animal.getImages() != null && !animal.getImages().isEmpty()) {
	        animalImageRepository.deleteAll(animal.getImages());
	        
	        animal.getImages().clear();
	        
	        animalRepository.save(animal);
	    }
	    
	    return org.springframework.http.ResponseEntity.ok().body("Image cleared successfully from database registry.");
	}
	
	
}
