package com.lifeofpaw.project.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "ANIMALS")
public class Animal {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "animal_seq_gen")
	@SequenceGenerator(name = "animal_seq_gen",sequenceName = "animals_seq",allocationSize = 1)
	@Column(name = "animal_id")
	private Long animalId;
	
	@NotBlank(message = "Animal name cannot be blank")
	@Size(max = 100,message = "Animal name cannot exceed 100 characters")
	@Column(nullable = false,length = 100)
	private String name;
	
	@NotBlank(message = "Species cannot be blank")
	@Size(max = 50,message = "Species field cannot exceed 50 characters")
	@Column(nullable = false,length = 50)
	private String species;
	
	@Column(length = 100)
	private String breed;
	
	@NotBlank(message = "Age category cannot be blank (e.g., Puppy, Kitten, Adult)")
	@Column(name = "age_category",length = 20)
	private String ageCategory;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "medical_history", columnDefinition = "TEXT")
	private String medicalHistory;
	
	@Column(length = 30)
	private String status="AVAILABLE";
	
	@Column(name = "added_date",updatable = false)
	private LocalDateTime addedDate=LocalDateTime.now();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id",nullable = false)
	@JsonBackReference
	private Organizations organization;
	
	@NotBlank(message = "Gender cannot be blank")
	@Column(length = 10)
	private String gender;
	
	@OneToMany(mappedBy = "animal",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
	private List<AnimalImage> images;

}

