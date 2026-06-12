package com.lifeofpaw.project.entity;

import java.time.LocalDateTime;

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
@Table(name = "REVIEWS")
@Data
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "rev_seq")
	@SequenceGenerator(name = "rev_seq",sequenceName = "reviews_seq",allocationSize = 1)
	private Long reviewId;
	
	@ManyToOne
	@JoinColumn(name = "reviewer_id")
	private User reviewer;
	
	@ManyToOne
	@JoinColumn(name = "org_id")
	private Organizations organization;
	
	private Integer rating;
	
	@Lob
	private String commentText;

	private LocalDateTime createdAt=LocalDateTime.now();

	
	
}
