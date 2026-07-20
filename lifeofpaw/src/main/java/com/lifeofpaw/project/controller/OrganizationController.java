package com.lifeofpaw.project.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lifeofpaw.project.entity.Organizations;
import com.lifeofpaw.project.entity.OrganizationImage;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.UserRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.service.OrganizationService;
import com.lifeofpaw.project.service.FileStorageService;

@RestController
@RequestMapping("/api/orgs")
public class OrganizationController {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileStorageService fileStorageService;
	
	@PostMapping("/add")
	public Organizations addOrg(
			@RequestParam("orgName") String orgName,
			@RequestParam("licenseNumber") String licenseNumber,
			@RequestParam("location") String location,
			@RequestParam("sanctuaryDescription") String sanctuaryDescription,
			@RequestParam(value = "files", required = false) MultipartFile[] files, 
			@RequestParam Long userId, 
			Principal principal) {
		
		if (files != null && files.length > 3) {
			throw new IllegalArgumentException("SECURITY/STORAGE VIOLATION: You can upload a maximum of 3 sanctuary gallery images!");
		}
		
		Organizations org = new Organizations();
		org.setOrgName(orgName);
		org.setLicenseNumber(licenseNumber);
		org.setLocation(location);
		org.setSanctuaryDescription(sanctuaryDescription);
		org.setIsVerified("N");

		List<OrganizationImage> imageList = new ArrayList<>();
		if (files != null && files.length > 0) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					String savedUrlPath = fileStorageService.storeFile(file, "organizations");
					
					OrganizationImage orgImage = new OrganizationImage();
					orgImage.setImageUrl(savedUrlPath);
					orgImage.setOrganization(org);
					imageList.add(orgImage);
				}
			}
		}
		

		org.setGalleryImages(imageList);
		
		return organizationService.saveOrganization(org, userId, principal.getName());
	}
	
	@PatchMapping("/{id}/verify")
	public String verifyOrg(@PathVariable Long id) {
		organizationService.verifyOrganization(id);
		return "Organization " + id + " has been successfully verified!";
	}
	
	@DeleteMapping("/{id}")
	public String deleteOrg(@PathVariable Long id) {
		organizationService.deleteOrganization(id);
		return "Organization " + id + " and all its animal listings have been removed.";
	}
	
	@GetMapping("/all")
	public List<Organizations> getAllOrgs(){
		return organizationService.getAllOrganizations();
	}
	
	@GetMapping("/pending")
	public List<Organizations> getPendingOrgs() {
		return organizationService.getPendingOrganizations();
	}

	@GetMapping("/public/top")
	public List<Organizations> getTopOrganizations() {
		return organizationRepository.findTopOrganizationsByAnimalCount();
	}
}