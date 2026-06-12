package com.lifeofpaw.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifeofpaw.project.entity.AdoptionRequest;
import com.lifeofpaw.project.repository.AdoptionRepository;
import com.lifeofpaw.project.repository.AnimalRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AnimalRepository animalRepo;
	
	@Autowired
	private OrganizationRepository orgRepo;
	
	@Autowired
	private AdoptionRepository adoptionRepo;
	
	@GetMapping("/master-view")
	public Map<String, Object> getMasterDashboard(){
		
		Map<String, Object> response=new HashMap<>();
		
		
		Map<String, Long> stats=new HashMap<>();
		stats.put("totalUsers", userRepo.count());
		stats.put("totalAnimals", animalRepo.count());
		stats.put("totalOrganizations", orgRepo.count());
		stats.put("successfulAdoption", adoptionRepo.countByStatus("APPROVED"));
		stats.put("pendingAdoptions", adoptionRepo.countByStatus("PENDING"));
		
		response.put("stats", stats);
		
		List<AdoptionRequest> latestRequests=adoptionRepo.findLatestRequests();
		
		List<String> activities=latestRequests.stream().map(req->{
			String petName=req.getAnimal().getName();
			String userName=req.getUser().getFullName();
			String status=req.getStatus();
			
			if("APPROVED".equals(status)) {
				return petName + " was adopted successfully by " + userName + "!";
			}else {
				return "New adoption application received for " + petName + " from " + userName + ".";
			}
			
		}).collect(Collectors.toList());
		
		orgRepo.findLatestOrganization().forEach(org->
			activities.add("New Organization joined: " + org.getOrgName())
		);
	
		response.put("recentActivities", activities);
		
		return response;
		
	}
	
}
