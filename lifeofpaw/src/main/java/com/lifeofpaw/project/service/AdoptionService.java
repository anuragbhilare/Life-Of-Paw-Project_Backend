package com.lifeofpaw.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lifeofpaw.project.entity.AdoptionRequest;
import com.lifeofpaw.project.entity.Animal;
import com.lifeofpaw.project.entity.Organizations;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.AdoptionRepository;
import com.lifeofpaw.project.repository.AnimalRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.repository.UserRepository;


@Service
public class AdoptionService {

	@Autowired
	private AdoptionRepository adoptionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AnimalRepository animalRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	private void validateOrgOwnership(Long orgId,String currentUserEmail) {
		User user=userRepository.findByEmail(currentUserEmail)
				.orElseThrow(()->new RuntimeException("User not found"));
		
		if (user.getRole().equalsIgnoreCase("admin")) {
	        return;
	    }
		
		Organizations org=organizationRepository.findById(orgId)
				.orElseThrow(()->new RuntimeException("Organization not found"));
	
		if(org.getContactPerson()==null||org.getContactPerson().getUserId()!=user.getUserId()) {
			throw new RuntimeException("SECURITY VIOLATION: Access Denied! You do not own this organization dashboard.");
		}
	
	}
	
	@Transactional
	public AdoptionRequest applyForAdoption(Long userId, Long animalId, String reason, String location) {

		User user=userRepository.findById(userId)
				.orElseThrow(()->new RuntimeException("User not found"));
		
		Animal animal=animalRepository.findById(animalId)
				.orElseThrow(()->new RuntimeException("Animal not found"));

		if (animal.getOrganization() != null && animal.getOrganization().getContactPerson() != null) {
			if (animal.getOrganization().getContactPerson().getEmail().equalsIgnoreCase(user.getEmail()) ||
			    animal.getOrganization().getContactPerson().getUserId() == user.getUserId()) {
				throw new IllegalArgumentException("You cannot submit an adoption request for an animal listed by your own organization.");
			}
		}
	
		if("ADOPTED".equalsIgnoreCase(animal.getStatus())) {
			throw new RuntimeException("Adoption Failed: This beautiful pet has already been adopted by someone else!");
		}
		
		boolean alreadyApplied=adoptionRepository.checkDuplicateRequests(userId, animalId, java.util.Arrays.asList("PENDING","APPROVED"));
		
		if(alreadyApplied) {
			throw new RuntimeException("Adoption Failed: You already have an active (Pending or Approved) request for this animal!");
		}
		
		AdoptionRequest request=new AdoptionRequest();
		
		request.setUser(user);
		request.setAnimal(animal);
		request.setReason(reason);
		request.setDeliveryLocation(location);
		
		return adoptionRepository.saveAndFlush(request);
		
	}
	
	@Transactional
	public AdoptionRequest updateRequestStatus(Long requestId,String newStatus,String currentUserEmail) {
		
		User reviewer=userRepository.findByEmail(currentUserEmail)
				.orElseThrow(()->new RuntimeException("User not found"));
		
		AdoptionRequest request=adoptionRepository.findById(requestId)
				.orElseThrow(()->new RuntimeException("Request ID " + requestId + " not found"));
		
		if(!reviewer.getRole().equalsIgnoreCase("admin")) {
			Organizations animalOrg=request.getAnimal().getOrganization();
			
			if (animalOrg == null || animalOrg.getContactPerson() == null || 
		            animalOrg.getContactPerson().getUserId() != reviewer.getUserId()) {
		            throw new RuntimeException("SECURITY VIOLATION: You do not have permission to manage requests for this Organization!");
		     }
			
			if (request.getUser().getUserId() == reviewer.getUserId()) {
	            throw new RuntimeException("SECURITY VIOLATION: Conflict of Interest! You cannot approve or reject your own adoption request.");
	        }
			
		}
		
		request.setStatus(newStatus.toUpperCase());
		
		if("APPROVED".equalsIgnoreCase(newStatus)) {
			request.getAnimal().setStatus("ADOPTED");
		}
		
		return adoptionRepository.saveAndFlush(request);
		
	}
	
	@Transactional(readOnly = true)
	public List<AdoptionRequest> getUserAdoptionHistory(Long userId) {
		if(!userRepository.existsById(userId)) {
			throw new RuntimeException("User with ID " + userId + " not found.");
			
		}
		
		return adoptionRepository.findByUser_UserId(userId);
	}
	
	@Transactional(readOnly = true)
	public List<AdoptionRequest> getRequestForOrg(Long orgId, String currentUserEmail){
		validateOrgOwnership(orgId, currentUserEmail);
		return adoptionRepository.findByAnimal_Organization_OrgId(orgId);
	}
	
	@Transactional(readOnly = true)
	public List<AdoptionRequest> getPendingRequestForOrg(Long orgId, String currentUserEmail) {
		validateOrgOwnership(orgId, currentUserEmail);
		return adoptionRepository.findByStatusAndAnimal_Organization_OrgId("PENDING", orgId);
	}

}
