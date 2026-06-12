package com.lifeofpaw.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifeofpaw.project.entity.AnimalImage;

@Repository
public interface AnimalImageRepository extends JpaRepository<AnimalImage, Long> {

}
