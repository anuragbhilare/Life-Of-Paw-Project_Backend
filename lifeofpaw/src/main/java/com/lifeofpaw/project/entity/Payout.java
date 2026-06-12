package com.lifeofpaw.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "PAYOUTS")
@Data
public class Payout {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "pay_seq_gen")
	@SequenceGenerator(name = "pay_seq_gen",sequenceName = "payouts_seq",allocationSize = 1)
	private Long payoutId;
	
	@ManyToOne
	@JoinColumn(name = "org_id")
	private Organizations organization;
	
	private Double amount;
	private LocalDateTime payoutDate=LocalDateTime.now();
	private String remarks;
}
