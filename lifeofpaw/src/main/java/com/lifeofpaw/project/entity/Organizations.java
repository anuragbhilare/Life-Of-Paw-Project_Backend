package com.lifeofpaw.project.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "ORGANIZATIONS")
public class Organizations {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "org_seq_gen")
	@SequenceGenerator(name = "org_seq_gen",sequenceName = "organizations_seq",allocationSize = 1)
	private Long orgId;
	
	@NotBlank(message = "Organization name cannot be blank")
	@Size(max = 150, message = "Organization name cannot exceed 150 characters")
	@Column(name = "org_name",nullable = false,length = 150)
	private String orgName;
	
	@NotBlank(message = "License number cannot be blank")
	@Size(max = 50, message = "License number cannot exceed 50 characters")
	@Column(name = "license_number",unique = true,length = 50)
	private String licenseNumber;
	
	@Column(name = "is_verified" ,length =1 )
	private String isVerified="N";
	
	@NotBlank(message = "Location cannot be blank")
	@Column(length = 250)
	private String location;
	
	@Column(name = "sanctuary_description", length = 1000)
	private String sanctuaryDescription;
	
	@ManyToOne
	@JoinColumn(name = "contact_person_id", nullable = false)
	private User contactPerson;
	
	@OneToMany(mappedBy = "organization" , cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Animal> animals;
	
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<OrganizationImage> galleryImages = new ArrayList<>();
}
