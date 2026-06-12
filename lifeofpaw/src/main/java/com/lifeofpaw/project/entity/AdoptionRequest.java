package com.lifeofpaw.project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@Table(name = "ADOPTION_REQUESTS")
@Data
public class AdoptionRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "adopt_seq_gen")
	@SequenceGenerator(name = "adopt_seq_gen", sequenceName = "adopt_req_seq", allocationSize = 1)
	@Column(name = "request_id")
	private Long requestId;
	
	@ManyToOne
	@JoinColumn(name = "user_id",nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "animal_id",nullable = false)
	private Animal animal;
	
	@Column(length = 20)
	private String status="PENDING";
	
	@Lob
	private String reason;
	
	@Column(name = "delivery_location",length = 500)
	private String deliveryLocation;
	
	@Column(name = "request_date",updatable = false)
	private LocalDateTime requestDate=LocalDateTime.now();
	
}
