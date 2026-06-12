package com.lifeofpaw.project.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeofpaw.project.entity.Donation;
import com.lifeofpaw.project.entity.Payout;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.UserRepository;
import com.lifeofpaw.project.service.FinanceService;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

	@Autowired
	private FinanceService financeService;
	
	@Autowired
	private UserRepository userRepository;


	@PostMapping("/donate")
	public Donation donate(Principal principal,@RequestParam double amount) {
		User user=userRepository.findByEmail(principal.getName()).get();

		return financeService.acceptDonation(user.getUserId(), amount);
	}
	
	
	@PostMapping("/admin/payout")
	public Payout payout(@RequestParam Long orgId,@RequestParam Double amount, @RequestParam String remarks) {
		return financeService.distributeFunds(orgId, amount, remarks);
	}
	
	@GetMapping("/ngo/my-payouts")
	public List<Payout> ngoHistory(@RequestParam Long orgId, Principal principal){
		return financeService.getNgoHistory(orgId,principal.getName());
	}
	
	@GetMapping("/my-donation-history")
	public List<Donation> getMyDonationHistory(Principal principal){
		User user=userRepository.findByEmail(principal.getName())
				.orElseThrow(()->new RuntimeException("Logged in user not found"));
		
		return financeService.getUserHistory(user.getUserId());
	}
	
	@GetMapping("/admin/all-donations")
	public List<Map<String, Object>> getAllDonationsForAdmin() {
		return financeService.getAllDonationsWithDonorDetails();
	}

	@GetMapping("/admin/savings-summary")
	public Map<String, Object> getPlatformSavingsSummary() {
		return financeService.getPlatformSavingsSummary();
	}
	
	@GetMapping("/admin/all-payouts")
	public List<Map<String, Object>> getAllPayoutsForAdmin() {
		return financeService.getAllPayoutsWithOrgDetails();
	}
	
	
}
