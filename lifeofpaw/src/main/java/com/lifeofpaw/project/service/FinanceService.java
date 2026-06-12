package com.lifeofpaw.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifeofpaw.project.entity.Donation;
import com.lifeofpaw.project.entity.Organizations;
import com.lifeofpaw.project.entity.Payout;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.DonationRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.repository.PayoutRepository;
import com.lifeofpaw.project.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;


@Service
public class FinanceService {

	@Autowired
	private DonationRepository donationRepository;
	
	@Autowired
	private PayoutRepository payoutRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	
	
	private void validateFinanceOwnership(Long orgId,String currentuserEmail) {
		User user =userRepository.findByEmail(currentuserEmail)
				.orElseThrow(()->new RuntimeException("User not found"));
		
		if(user.getRole().equalsIgnoreCase("admin")){
			return;
			
		}
		
		Organizations org=organizationRepository.findById(orgId)
				.orElseThrow(()->new RuntimeException("Organization not found"));
		
		if(org.getContactPerson()==null || org.getContactPerson().getUserId()!=user.getUserId()) {
			throw new RuntimeException("SECURITY VIOLATION: Access Denied! You are not authorized to view financial logs for this organization.");
		}
	}


	@Transactional
	public Donation acceptDonation(Long userId, Double amount) {
		User donor=userRepository.findById(userId)
				.orElseThrow(()->new RuntimeException("User not found"));
		
		Donation d=new Donation();
		d.setDonor(donor);
		d.setAmount(amount);
		return donationRepository.save(d);
	}

	@Transactional
	public Payout distributeFunds(Long orgId, Double amount,String remarks) {
		Double totalIn=donationRepository.getTotalPlatformFunds();
		Double totalOut=payoutRepository.getTotalDistributedFunds();
		
		double currentPool = (totalIn != null ? totalIn : 0) - (totalOut != null ? totalOut : 0);
	
		if(currentPool<amount) {
			throw new RuntimeException("Insufficient Funds in Platform Pool! Balance: " + currentPool);
		}
		
		Organizations org=organizationRepository.findById(orgId)
				.orElseThrow(()->new RuntimeException("Org not found"));
		Payout p=new Payout();
		p.setOrganization(org);
		p.setAmount(amount);
		p.setRemarks(remarks);
		return payoutRepository.save(p);
		
	}
	
	public List<Donation> getUserHistory(Long userId) { 
		return donationRepository.findByDonor_UserId(userId);
	}
	
    public List<Payout> getNgoHistory(Long orgId, String currentuseremail) {
    	validateFinanceOwnership(orgId, currentuseremail);
    	return payoutRepository.findByOrganization_OrgId(orgId);
    }
    
    public Map<String, Object> getPlatformSavingsSummary() {
		Double totalIn = donationRepository.getTotalPlatformFunds();
		Double totalOut = payoutRepository.getTotalDistributedFunds();
		
		double totalDonations = (totalIn != null) ? totalIn : 0.0;
		double totalPayouts = (totalOut != null) ? totalOut : 0.0;
		double currentSavings = totalDonations - totalPayouts;
		
		Map<String, Object> summary = new HashMap<>();
		summary.put("totalDonationsRaised", totalDonations);
		summary.put("totalPayoutsDistributed", totalPayouts);
		summary.put("netSavingsBalance", currentSavings);
		
		return summary;
	}

	
    public List<Map<String, Object>> getAllDonationsWithDonorDetails() {
		return donationRepository.findAllOrderByTransactionDateDesc().stream().map(donation -> {
			Map<String, Object> map = new HashMap<>();
			map.put("donationId", donation.getDonationId());
			map.put("amount", donation.getAmount());
			map.put("transactionDate", donation.getTransactionDate());
			map.put("status", donation.getStatus());
			
			if (donation.getDonor() != null) {
				map.put("donorId", donation.getDonor().getUserId());
				map.put("donorName", donation.getDonor().getFullName());
				map.put("donorEmail", donation.getDonor().getEmail());
			} else {
				map.put("donorName", "Anonymous Donor");
			}
			return map;
		}).collect(Collectors.toList());
	}
    
    public List<Map<String, Object>> getAllPayoutsWithOrgDetails() {
		return payoutRepository.findAllOrderByPayoutDateDesc().stream().map(payout -> {
			Map<String, Object> map = new HashMap<>();
			map.put("payoutId", payout.getPayoutId());
			map.put("amount", payout.getAmount());
			map.put("payoutDate", payout.getPayoutDate());
			map.put("remarks", payout.getRemarks());
			
			if (payout.getOrganization() != null) {
				map.put("orgId", payout.getOrganization().getOrgId());
				map.put("orgName", payout.getOrganization().getOrgName());
			} else {
				map.put("orgName", "Unknown Organization");
			}
			return map;
		}).collect(Collectors.toList());
	}

}
