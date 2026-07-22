package com.lifeofpaw.project.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeofpaw.project.entity.Message;
import com.lifeofpaw.project.entity.Review;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.ReviewRepository;
import com.lifeofpaw.project.repository.UserRepository;
import com.lifeofpaw.project.service.FileStorageService;
import com.lifeofpaw.project.service.SocialService;

@RestController
@RequestMapping("/api/social")
public class SocialController {

	@Autowired
	private SocialService socialService;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@PostMapping("/reviews/add")
	public Review addReview(Principal principal,
							@RequestParam Long orgId,
							@RequestParam Integer rating,
							@RequestParam String comment) {
		User user=userRepository.findByEmail(principal.getName()).get();
		return socialService.leaveReview(user.getUserId(), orgId, rating, comment);
		
	}
	
	@PostMapping("/messages/send-private")
	public Message sendDM(Principal principal,
							@RequestParam Long receiverId,
							@RequestParam String content) {
		User user=userRepository.findByEmail(principal.getName()).get();
		return socialService.sendMessage(user.getUserId(), receiverId, content, "PRIVATE", null);
	}
	
	@GetMapping("/messages/chat-history")
	public List<Message> getChat(Principal principal,
								 @RequestParam Long otherUserId){
		User user=userRepository.findByEmail(principal.getName()).get();
		return socialService.getPrivateChat(user.getUserId(), otherUserId);
	}
	
	@PostMapping("/post-to-feed")
	public Message postToFeed(Principal principal,
								@RequestParam String content,
								@RequestParam String type,
								@RequestParam(value = "file",required = false) org.springframework.web.multipart.MultipartFile file) {
	
		User user=userRepository.findByEmail(principal.getName()).get();
		
		String generatedUrlPath = null;
		if(file!=null && !file.isEmpty()) {
			generatedUrlPath = fileStorageService.storeFile(file, "community");
		}
		
		return socialService.sendMessage(user.getUserId(), null, content, type, generatedUrlPath);
	}
	
	@GetMapping("/community-feed")
	public List<Message> getFeed(){
		return socialService.getCommunityFeed();
	}
	
	
	@DeleteMapping("/reviews/{reviewId}")
	public String deleteReview(@PathVariable Long reviewId, Principal principal) {
		socialService.deleteReview(reviewId,principal.getName());
		return "Review deleted successfully";
	}
	
	@GetMapping("/reviews/org/{orgId}")
	public List<Review> getOrgReviews(@PathVariable Long orgId){
		return reviewRepository.findByOrganization_OrgId(orgId);
	}
	
	@GetMapping("/reviews/all")
	public List<Review> getAllReviews(){
		return socialService.getAllReviews();
	}
	
	@PostMapping("/messages/organization/reply")
	public Message orgreplyToUser(Principal principal,
									@RequestParam Long userTargetedId,
									@RequestParam String content) {
		User orgUser=userRepository.findByEmail(principal.getName())
				.orElseThrow(()->new RuntimeException("Logged in Organization account not found"));
		
		if("USER".equalsIgnoreCase(orgUser.getRole())) {
			throw new RuntimeException("Access Denied: Only Organizations or Admins can access this endpoint.");
		}
		
		return socialService.sendMessage(orgUser.getUserId(), userTargetedId, content, "PRIVATE", null);
	}
	
	@GetMapping("/history/messages")
	public List<Message> getMyMessagesHistory(Principal principal) {
		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("Logged in user not found"));
		return socialService.getUserMessagesHistory(user.getUserId());
	}

	@GetMapping("/history/donations")
	public List<com.lifeofpaw.project.entity.Donation> getMyDonationHistory(Principal principal) {
		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("Logged in user not found"));
		return socialService.getUserDonationHistory(user.getUserId());
	}

	@GetMapping("/history/adoptions")
	public List<com.lifeofpaw.project.entity.AdoptionRequest> getMyAdoptionHistory(Principal principal) {
		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("Logged in user not found"));
		return socialService.getUserAdoptionHistory(user.getUserId());
	}
	
	@PostMapping("/messages/send-to-admin")
	public Message sendToAdmin(Principal principal, @RequestParam String content) {
		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("Logged in user not found"));
		
		if ("admin".equalsIgnoreCase(user.getRole())) {
			throw new RuntimeException("Security Restriction: Administrators cannot send support requests to themselves.");
		}
		
		return socialService.sendSupportMessageToAdmin(user.getUserId(), content);
	}

	@GetMapping("/messages/admin/support-feed")
	public List<Message> getAdminSupportFeed(Principal principal) {
		User adminUser = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("Logged in account not found"));
		
		if (!"admin".equalsIgnoreCase(adminUser.getRole())) {
			throw new RuntimeException("Access Denied: This dashboard view is restricted to Administrators only.");
		}
		
		return socialService.getAdminSupportMessages();
	}
	
	@PostMapping("/messages/admin/reply/{userId}")
	public Message adminReply(Principal principal, @PathVariable Long userId, @RequestParam String content) {
		User adminUser = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("Logged in Admin account not found"));
		
		
		if (!"admin".equalsIgnoreCase(adminUser.getRole())) {
			throw new RuntimeException("Access Denied: Only administrators can reply to support tickets.");
		}
		
		return socialService.adminReplyToUser(adminUser.getUserId(), userId, content);
	}
	
	@org.springframework.web.bind.annotation.PutMapping("/messages/read/{otherUserId}")
	public String markAsRead(Principal principal, @org.springframework.web.bind.annotation.PathVariable Long otherUserId) {
		User user = userRepository.findByEmail(principal.getName()).get();
		socialService.markMessagesAsRead(otherUserId, user.getUserId());
		return "Messages marked as read";
	}
}
