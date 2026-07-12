package com.lifeofpaw.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifeofpaw.project.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

	@Query("SELECT m FROM Message m WHERE (m.sender.userId = :u1 AND m.receiver.userId = :u2) " +
	           "OR (m.sender.userId = :u2 AND m.receiver.userId = :u1) ORDER BY m.createdAt ASC")
	List<Message> findDirectMessages(Long u1,Long u2);
	
	List<Message> findByMsgTypeInOrderByCreatedAtDesc(List<String> types);
	
	List<Message> findBySender_UserIdOrReceiver_UserIdAndMsgTypeOrderByCreatedAtDesc(Long senderId, Long receiverId, String msgType);
	
	@Query("SELECT m FROM Message m WHERE m.msgType = :msgType AND (m.receiver IS NULL OR m.receiver.role = 'admin') ORDER BY m.createdAt DESC")
	List<Message> findSupportMessages(@Param("msgType") String msgType);
}
