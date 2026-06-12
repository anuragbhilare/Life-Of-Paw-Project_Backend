package com.lifeofpaw.project.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.entity.Animal;
import com.lifeofpaw.project.entity.Organizations;
import com.lifeofpaw.project.repository.UserRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationRepository organizationRepository;
    
	@GetMapping("/connect")
	public String checkConnection() {
		return userService.checkConnection();
	}
	
	@PostMapping("/register")
	public User register(@jakarta.validation.Valid @RequestBody User user) {
		return userService.registerUser(user);
	}
	
	@GetMapping("/me")
	public User getMyProfile(Principal principal) {
		return userRepository.findByEmail(principal.getName())
				.orElseThrow(()->new RuntimeException("User not found"));
	}

	@GetMapping("/organization-status")
	public Map<String, Object> getOrganizationStatus(Principal principal) {
		Map<String, Object> response = new HashMap<>();
		User loggedInUser = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		List<Organizations> orgs = organizationRepository.findByContactPerson_UserId(loggedInUser.getUserId());
		if (orgs.isEmpty()) {
			response.put("hasApplication", false);
			response.put("status", null);
		} else {
			Organizations org = orgs.get(0);
			response.put("hasApplication", true);
			if ("Y".equalsIgnoreCase(org.getIsVerified()) || "APPROVED".equalsIgnoreCase(org.getIsVerified())) {
				response.put("status", "APPROVED");
			} else {
				response.put("status", "PENDING");
			}
			response.put("orgId", org.getOrgId());
			response.put("orgName", org.getOrgName());
		}
		return response;
	}
	
	@PutMapping("/me")
	public User updateMyProfile(Principal principal,@jakarta.validation.Valid @RequestBody User userDetails) {
		User loggedInUser=userRepository.findByEmail(principal.getName())
				.orElseThrow(()->new RuntimeException("User not found"));
		return userService.updateUser(loggedInUser.getUserId(), userDetails);
	}
	
	@DeleteMapping("/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "User " + id + " and all their adoption records have been removed.";
	}
	
	@GetMapping("/all")
	public List<User> getAll(){
		return userService.getAllUSers();
	}
	
	@GetMapping("/get-particular/{userId}")
	public User getParticularAnimal(@PathVariable Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("ERROR: User profile with ID " + userId + " does not exist in our registry."));
	}
	
	
}