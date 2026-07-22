package com.lifeofpaw.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lifeofpaw.project.entity.Message;
import com.lifeofpaw.project.entity.Organizations;
import com.lifeofpaw.project.entity.Review;
import com.lifeofpaw.project.entity.User;
import com.lifeofpaw.project.repository.AdoptionRepository;
import com.lifeofpaw.project.repository.DonationRepository;
import com.lifeofpaw.project.repository.MessageRepository;
import com.lifeofpaw.project.repository.OrganizationRepository;
import com.lifeofpaw.project.repository.ReviewRepository;
import com.lifeofpaw.project.repository.UserRepository;

@Service
public class SocialService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private DonationRepository donationRepository;
	
	@Autowired
	private AdoptionRepository adoptionRepository;
	
	@Transactional
	public Review leaveReview(Long userId,Long orgId,Integer rating,String comment) {
		User user=userRepository.findById(userId).get();
		Organizations org=organizationRepository.findById(orgId).get();
		Review r=new Review();
		r.setReviewer(user);
		r.setOrganization(org);
		r.setRating(rating);
		r.setCommentText(comment);
		return reviewRepository.save(r);
	}
	
	
	@Transactional
	public Message sendMessage(Long senderid,Long receiverId,String content,String type,String imageUrl) {
		User sender=userRepository.findById(senderid)
				.orElseThrow(()->new RuntimeException("Sender profile not found"));
		
		Message m=new Message();
		
		m.setSender(sender);
		m.setContent(content);
		m.setMsgType(type);
		m.setImageUrl(imageUrl);
		
		if("PRIVATE".equals(type) && receiverId!=null) {
			User receiver=userRepository.findById(receiverId)
					.orElseThrow(()->new RuntimeException("Receiver profile not found"));
			m.setReceiver(receiver);
		}
		
		return messageRepository.save(m);
	}
	
	public List<Message> getPrivateChat(Long u1,Long u2){
		return messageRepository.findDirectMessages(u1, u2);
	}


	public List<Message> getCommunityFeed() {
		return messageRepository.findByMsgTypeInOrderByCreatedAtDesc(java.util.Arrays.asList("COMMUNITY", "STRAY"));
	}


	public void deleteReview(Long reviewId,String currentUserEmail) {
		
		Review review=reviewRepository.findById(reviewId)
				.orElseThrow(()->new RuntimeException("Review not found"));
		
		User currentUser=userRepository.findByEmail(currentUserEmail)
				.orElseThrow(()->new RuntimeException("Logged in user not found"));
		
		if("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
			reviewRepository.delete(review);
			return;
		}
		
		boolean isReviewer = review.getReviewer() != null && 
		                     review.getReviewer().getUserId() == currentUser.getUserId();

		boolean isNgoOwner = review.getOrganization() != null && 
		                     review.getOrganization().getContactPerson() != null && 
		                     review.getOrganization().getContactPerson().getUserId() == currentUser.getUserId();
	
		if (!isReviewer && !isNgoOwner) {
	        throw new RuntimeException("SECURITY VIOLATION: Access Denied! You are not authorized to delete this review.");
	    }
		
		reviewRepository.delete(review);
	
	}

	public List<Review> getAllReviews(){
		return reviewRepository.findAll();
	}
	
	public List<Message> getUserMessagesHistory(Long userId) {
		return messageRepository.findBySender_UserIdOrReceiver_UserIdAndMsgTypeOrderByCreatedAtDesc(userId, userId, "PRIVATE");
	}

	public List<com.lifeofpaw.project.entity.Donation> getUserDonationHistory(Long userId) {
		return donationRepository.findByUser_UserIdOrderByDateDesc(userId);
	}

	public List<com.lifeofpaw.project.entity.AdoptionRequest> getUserAdoptionHistory(Long userId) {
		return adoptionRepository.findByUser_UserIdOrderByRequestIdDesc(userId);
	}
	
	@Transactional
	public Message sendSupportMessageToAdmin(Long senderId, String content) {
		User sender = userRepository.findById(senderId)
				.orElseThrow(() -> new RuntimeException("Sender profile not found"));
		
		Message m = new Message();
		m.setSender(sender);
		
		User adminUser = userRepository.findAll().stream()
				.filter(u -> "admin".equalsIgnoreCase(u.getRole()))
				.findFirst()
				.orElse(null);
				
		m.setReceiver(adminUser); 
		m.setContent(content);
		m.setMsgType("PRIVATE");
		m.setImageUrl(null);
		
		return messageRepository.save(m);
	}

	public List<Message> getAdminSupportMessages() {
		return messageRepository.findSupportMessages("PRIVATE");
	}

	
	
	@Transactional
	public Message adminReplyToUser(Long adminSenderId, Long targetUserId, String content) {
		User adminSender = userRepository.findById(adminSenderId)
				.orElseThrow(() -> new RuntimeException("Admin sender profile not found"));
		
		User targetReceiver = userRepository.findById(targetUserId)
				.orElseThrow(() -> new RuntimeException("Target user/organization not found"));
		
		Message m = new Message();
		m.setSender(adminSender);
		m.setReceiver(targetReceiver);
		m.setContent(content);
		m.setMsgType("PRIVATE"); 
		m.setImageUrl(null);
		
		return messageRepository.save(m);
	}
	
	@Transactional
	public void markMessagesAsRead(Long senderId, Long receiverId) {
		List<Message> messages = messageRepository.findDirectMessages(senderId, receiverId);
		for (Message m : messages) {
			if (m.getReceiver() != null && m.getReceiver().getUserId() == receiverId &&
			    m.getSender() != null && m.getSender().getUserId() == senderId && !m.isRead()) {
				m.setRead(true);
				messageRepository.save(m);
			}
		}
	}
}
