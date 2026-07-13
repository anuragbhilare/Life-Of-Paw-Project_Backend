package com.lifeofpaw.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AnimalRepository animalRepository;
	
	@Autowired
	private AdoptionRepository adoptionRepository;
	
	public Organizations saveOrganization(Organizations org, Long userId, String loggedInUserEmail) {
		
		User loggedInUser=userRepository.findByEmail(loggedInUserEmail)
				.orElseThrow(()->new RuntimeException("Authentication session user not found!"));
		
		if(loggedInUser.getUserId()!=userId) {
			throw new RuntimeException("SECURITY ALERT: You cannot register an organization for another user's ID!");
		}
		
		User existingUser=userRepository.findById(userId)
				.orElseThrow(()->new RuntimeException("User with ID " + userId + " not found!"));
		
		org.setContactPerson(existingUser);		
		
		return organizationRepository.save(org);
	}
	
	@Transactional
	public Organizations verifyOrganization(Long orgId) {
		Organizations org=organizationRepository.findById(orgId)
				.orElseThrow(()->new jakarta.persistence.EntityNotFoundException("Organization not found with ID: " + orgId));
		
		org.setIsVerified("Y");
		
		User owner=org.getContactPerson();
		if(owner!=null) {
			owner.setRole("org");
			userRepository.save(owner);
		}
		return organizationRepository.save(org);
	}
	
	@Transactional
	public void deleteOrganization(Long orgId) {
		
		Organizations org=organizationRepository.findById(orgId)
				.orElseThrow(()->new jakarta.persistence.EntityNotFoundException("Cannot delete. Organization ID " + orgId + " does not exist."));
		
		List<Animal> animals=animalRepository.findByOrganization_OrgId(orgId);
		for(Animal animal : animals) {
			adoptionRepository.deleteByAnimal_AnimalId(animal.getAnimalId());
		}
		
		organizationRepository.delete(org);
	}
	
	public List<Organizations> getAllOrganizations() {
		List<Organizations> orgs = organizationRepository.findAllWithImages();
		
		for (Organizations org : orgs) {
			if (org.getIsVerified() == null || org.getIsVerified().trim().isEmpty()) {
				org.setIsVerified("N");
			}
		}
		
		return orgs;
	}
	
	public List<Organizations> getPendingOrganizations() {
		return organizationRepository.findByIsVerifiedWithImages("N");
	}

}
