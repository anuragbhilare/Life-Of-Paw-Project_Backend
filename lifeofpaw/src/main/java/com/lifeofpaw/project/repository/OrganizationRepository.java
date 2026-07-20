package com.lifeofpaw.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifeofpaw.project.entity.Organizations;


@Repository
public interface OrganizationRepository extends JpaRepository<Organizations, Long>{

	List<Organizations> findByContactPerson_UserId(Long userId);

	
	@Query(value = """
		    SELECT *
		    FROM organizations
		    ORDER BY org_id DESC
		    LIMIT 3
		    """, nativeQuery = true)
	List<Organizations> findLatestOrganization();
	
	
	@Query("SELECT DISTINCT o FROM Organizations o LEFT JOIN FETCH o.galleryImages ORDER BY o.orgId DESC")
	List<Organizations> findAllWithImages();
	
	@Query("SELECT DISTINCT o FROM Organizations o LEFT JOIN FETCH o.galleryImages WHERE o.isVerified = :status ORDER BY o.orgId DESC")
	List<Organizations> findByIsVerifiedWithImages(@Param("status") String status);

	@Query("SELECT o FROM Organizations o LEFT JOIN o.animals a WHERE o.isVerified = 'Y' GROUP BY o.orgId ORDER BY COUNT(a) DESC LIMIT 4")
	List<Organizations> findTopOrganizationsByAnimalCount();
}
