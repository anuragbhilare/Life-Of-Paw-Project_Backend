package com.lifeofpaw.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "DONATIONS")
@Data
public class Donation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "don_seq_gen")
	@SequenceGenerator(name = "don_seq_gen",sequenceName = "donations_seq",allocationSize = 1)
	private Long donationId;

	@ManyToOne
	@JoinColumn(name = "donor_id")
	private User donor;
	
	private double amount;
	private LocalDateTime transactionDate=LocalDateTime.now();
	private String status="SUCCESS";
}
