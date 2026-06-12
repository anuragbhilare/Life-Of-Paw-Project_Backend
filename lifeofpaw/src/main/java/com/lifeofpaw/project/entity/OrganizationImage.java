package com.lifeofpaw.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "ORGANIZATION_IMAGES")
@Data
public class OrganizationImage{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_img_seq_gen")
	@SequenceGenerator(name = "org_img_seq_gen", sequenceName = "organization_images_seq", allocationSize = 1)
	private Long imageId;

	private String imageUrl; // This holds the generated path string from FileStorageService

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id", nullable = false)
	@JsonBackReference
	private Organizations organization;
}