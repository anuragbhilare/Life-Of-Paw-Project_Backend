package com.lifeofpaw.project.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeofpaw.project.entity.AdoptionRequest;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.AdoptionRepository;
import com.lifeofpaw.project.repository.UserRepository;
import com.lifeofpaw.project.service.AdoptionService;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {

	@Autowired
	private AdoptionService adoptionService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdoptionRepository adoptionRepository;
	
	@PostMapping("/apply")
	public AdoptionRequest apply(
			Principal principal,
			@RequestParam Long animalId,
			@RequestParam String reason,
			@RequestParam String location
			) {
		User user=userRepository.findByEmail(principal.getName())
				.orElseThrow(()->new RuntimeException("Logged in user not found"));
		return adoptionService.applyForAdoption(user.getUserId(), animalId, reason, location);
	}
	
	@GetMapping("/my-history")
	public List<AdoptionRequest> getMyHistory(Principal principal){
		User user=userRepository.findByEmail(principal.getName())
				.orElseThrow(()->new RuntimeException("Logged in user not found"));
		return adoptionService.getUserAdoptionHistory(user.getUserId());
	}
	@GetMapping("/all")
	public List<AdoptionRequest> getAllRequests() {
	    return adoptionRepository.findAll();
	}
	
	@PutMapping("/update-status")
	public AdoptionRequest updateStatus(
			@RequestParam Long requestId,
			@RequestParam String status,
			java.security.Principal principal) {
		return adoptionService.updateRequestStatus(requestId, status, principal.getName());
	}
	
}
