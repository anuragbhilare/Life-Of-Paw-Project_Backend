package com.lifeofpaw.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifeofpaw.project.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

	List<Review> findByOrganization_OrgId(Long orgId);
}
