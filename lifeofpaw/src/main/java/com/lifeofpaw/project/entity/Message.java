package com.lifeofpaw.project.entity;

import java.sql.Types;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MESSAGES")
@Data
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "msg_seq")
	@SequenceGenerator(name = "msg_seq",sequenceName = "messages_seq",allocationSize = 1)
	private Long msgId;
	
	@ManyToOne
	@JoinColumn(name = "sender_id")
	private User sender;
	
	@ManyToOne
	@JoinColumn(name = "receiver_id")
	private User receiver;
	
	
	@JdbcTypeCode(Types.LONGVARCHAR)
	private String content;

	private String imageUrl;
	
	private String msgType;
	
	private LocalDateTime createdAt=LocalDateTime.now();

	@com.fasterxml.jackson.annotation.JsonProperty("isRead")
	private boolean isRead = false;



}
