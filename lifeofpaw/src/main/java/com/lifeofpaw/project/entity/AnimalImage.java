package com.lifeofpaw.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ANIMAL_IMAGES")
@Data
public class AnimalImage {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "img_seq")
	@SequenceGenerator(name = "img_seq",sequenceName = "animal_images_seq",allocationSize = 1)
	private Long imageId;

	private String imageUrl;

	@ManyToOne
	@JoinColumn(name = "animal_id")
	@JsonBackReference
	private Animal animal;
}
