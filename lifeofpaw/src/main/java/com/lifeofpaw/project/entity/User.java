package com.lifeofpaw.project.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name="APP_USERS")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
	@SequenceGenerator(name = "user_seq_gen", sequenceName = "app_users_seq", allocationSize = 1)
	private long userId;
	
	@NotBlank(message = "Full name cannot be blank")
	@Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
	@Column(name = "full_name", nullable = false, length = 100)
	private String fullName;
	
	@NotBlank(message = "Email address cannot be blank")
	@Email(message = "Please provide a valid email address")
	@Column(nullable = false,unique = true,length = 100)
	private String email;
	
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	@Column(nullable = false,length = 255)
	private String password;
	
	@NotBlank(message = "Phone number cannot be blank")
	@Size(max = 20, message = "Phone number cannot exceed 20 characters")
	@Column(length = 20,unique = true)
	private String phone;
	
	@Column(length = 20)
	private String role="user";

	@Column(name = "created_at",updatable = false)
	private LocalDateTime createdAt=LocalDateTime.now();
	
	@OneToMany(mappedBy = "contactPerson",cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonIgnore
	private List<Organizations> organizations;
	
}