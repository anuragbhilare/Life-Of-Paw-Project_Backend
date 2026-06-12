package com.lifeofpaw.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lifeofpaw.project.entity.Animal;
import com.lifeofpaw.project.entity.Organizations;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.AdoptionRepository;
import com.lifeofpaw.project.repository.AnimalRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.repository.UserRepository;

@Service
public class AnimalService {

	@Autowired
	private AnimalRepository animalRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private AdoptionRepository adoptionRepository;
	@Autowired
	private UserRepository userRepository;
	
	private void validateOrgOwnership(Long orgId,String currentUserEmail) {
		User user=userRepository.findByEmail(currentUserEmail)
				.orElseThrow(()->new RuntimeException("User not found"));
		
		if(user.getRole().equalsIgnoreCase("admin")) {
			return;
		}
		
		Organizations org=organizationRepository.findById(orgId)
				.orElseThrow(()->new RuntimeException("Organization not found"));
		
		if(org.getContactPerson()==null||org.getContactPerson().getUserId()!=user.getUserId()) {
			throw new RuntimeException("SECURITY VIOLATION: Access Denied! You do not own this organization dashboard.");
		}
	}
	
	
	@Transactional
	public Animal addAnimal(Animal animal, Long orgId, String currentuserEmail) {
		
		animal.setStatus("AVAILABLE");;
		
		User user=userRepository.findByEmail(currentuserEmail)
				.orElseThrow(()->new RuntimeException("User not found"));
		
		Organizations org=organizationRepository.findById(orgId)
				.orElseThrow(() -> new RuntimeException("Organization not found"));
		
		if(!user.getRole().equalsIgnoreCase("admin")) {
			if(org.getContactPerson()== null || org.getContactPerson().getUserId() != user.getUserId()) {
				throw new RuntimeException("SECURITY VIOLATION: You can only add animals to your own Organization!");			}
		}
		
		boolean duplicateExists=animalRepository.checkDuplicateAnimal(
				animal.getName(),
				animal.getSpecies(),
				animal.getGender(),
				"AVAILABLE",
				orgId);
		
		if(duplicateExists) {
			throw new IllegalArgumentException("An available animal profile named '" + animal.getName() + "' already exists for your organization.");
		}
		
		
		animal.setOrganization(org);
		return animalRepository.save(animal);
	}
	
	
	public List<Animal> getAllAvailableAnimals(){
		return animalRepository.findByStatus("AVAILABLE");
	}
	
	public List<Animal> searchAnimal(String species,String gender, String status){
		String searchStatus=(status==null)? "AVAILABLE" : status;
		return animalRepository.findBySpeciesIgnoreCaseAndGenderIgnoreCaseAndStatusIgnoreCase(species, gender, searchStatus);
	}
	
	@Transactional
	public Animal updateAnimal(Long id,Animal updatedDetails) {
		Animal existingAnimal=animalRepository.findById(id)
				.orElseThrow(()->new RuntimeException("Animal not found with id: " + id));
		
		existingAnimal.setName(updatedDetails.getName());
		existingAnimal.setSpecies(updatedDetails.getSpecies());
		existingAnimal.setBreed(updatedDetails.getBreed());
		existingAnimal.setGender(updatedDetails.getGender());
		existingAnimal.setAgeCategory(updatedDetails.getAgeCategory());
		existingAnimal.setDescription(updatedDetails.getDescription());
		
		return animalRepository.save(existingAnimal);
	}
	
	@Transactional
	public void deleteAnimal(Long id) {
		if(!animalRepository.existsById(id)) {
			throw new RuntimeException("Cannot delete. Animal ID " + id + " does not exist.");
		}
		
		adoptionRepository.deleteByAnimal_AnimalId(id);
		
		animalRepository.deleteById(id);
	}
	
	public List<Animal> getAllAnimals() {

		return animalRepository.findAll();

		}
	
	public List<Animal> getAnimalsByOrg(Long orgId, String currentUserEmail){
		validateOrgOwnership(orgId, currentUserEmail);
		return animalRepository.findByOrganization_orgId(orgId);
	}
	
	
}
