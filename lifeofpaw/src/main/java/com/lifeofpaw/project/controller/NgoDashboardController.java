package com.lifeofpaw.project.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifeofpaw.project.entity.AdoptionRequest;
import com.lifeofpaw.project.entity.Animal;
import com.lifeofpaw.project.service.AdoptionService;
import com.lifeofpaw.project.service.AnimalService;

@RestController
@RequestMapping("/api/ngo/dashboard")
public class NgoDashboardController {

	@Autowired
	private AnimalService animalService;
	
	@Autowired
	private AdoptionService adoptionService;
	
	
	@GetMapping("/{orgId}/animals")
	public List<Animal> getMyAnimals(@PathVariable Long orgId,Principal principal){
		return animalService.getAnimalsByOrg(orgId,principal.getName());
	}
	
	@GetMapping("/{orgId}/requests")
	public List<AdoptionRequest> getMyRequests(@PathVariable Long orgId,Principal principal){
		return adoptionService.getRequestForOrg(orgId,principal.getName());
	}
	
	@GetMapping("/{orgId}/pending-inbox")
	public List<AdoptionRequest> getPendingInbox(@PathVariable Long orgId,Principal principal){
		return adoptionService.getPendingRequestForOrg(orgId,principal.getName());
	}
}
