package com.lifeofpaw.project.service;

import com.lifeofpaw.project.entity.Organizations;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.AdoptionRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	
	@Autowired
	private AdoptionRepository adoptionRepository;

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public User registerUser(User user) {
		
		user.setEmail(user.getEmail().trim().toLowerCase());
		
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new IllegalArgumentException("An account with this email address already exists.");
		}
		
		if(userRepository.existsByPhone(user.getPhone())) {
			throw new IllegalArgumentException("This mobile number is already registered by another user.");
		}
		
		String encodedPassword=passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		user.setRole("user");
		
		return userRepository.save(user);
	}
	
	@Transactional
	public void deleteUser(Long userId) {
		User user=userRepository.findById(userId)
			.orElseThrow(()->new RuntimeException("User not found with ID: " + userId));
		
		adoptionRepository.deleteByUser_UserId(userId);
		
		List<Organizations> orgs=organizationRepository.findByContactPerson_UserId(userId);
		
		for(Organizations org:orgs) {
			org.setContactPerson(null);
			organizationRepository.save(org);
		}
		
		userRepository.delete(user);
	}
	
	public List<User> getAllUSers(){
		return userRepository.findAll();
	}
	
	@Transactional
	public User updateUser(Long id,User details) {
		User user=userRepository.findById(id)
				.orElseThrow(()->new RuntimeException("User not found with ID: " + id));
		
		if(details.getEmail()!=null && !details.getEmail().equalsIgnoreCase(user.getEmail())) {
			boolean emailExists = userRepository.findByEmail(details.getEmail()).isPresent();
			if(emailExists) {
				throw new RuntimeException("Update Failed: This email address is already registered to another account!");
			}
			user.setEmail(details.getEmail());
		}
		

		if(details.getFullName()!=null){
		    user.setFullName(details.getFullName());
		}
		
		if(details.getPhone()!=null) {
		user.setPhone(details.getPhone());
		}
		
		if(details.getPassword()!=null && !details.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(details.getPassword()));
		}
		
		return userRepository.save(user);
	}
	
	
	
	
	public String checkConnection() {
	
		try {
		long count=userRepository.count();
		return "Database Connection Successful & Total Users : "+count;
		}catch(Exception e){
			return "Connection Failed : "+e.getMessage();
		}
	}


	

    
}