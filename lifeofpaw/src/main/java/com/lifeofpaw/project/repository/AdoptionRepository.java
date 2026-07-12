package com.lifeofpaw.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lifeofpaw.project.entity.AdoptionRequest;

@Repository
public interface AdoptionRepository extends JpaRepository<AdoptionRequest, Long>{

	List<AdoptionRequest> findByUser_UserId(Long userId);	
	
	void deleteByAnimal_AnimalId(Long animalId);
	
	@Transactional
	void deleteByUser_UserId(Long userId);
	
	List<AdoptionRequest> findByAnimal_Organization_OrgId(Long orgId);

	List<AdoptionRequest> findByStatusAndAnimal_Organization_OrgId(String status,Long orgId);

	Long countByStatus(String status);
	
	@Query(value = """
		    SELECT *
		    FROM ADOPTION_REQUESTS
		    ORDER BY REQUEST_DATE DESC
		    LIMIT 5
		    """, nativeQuery = true)
	List<AdoptionRequest> findLatestRequests();

	@Query("SELECT COUNT(a) > 0 FROM AdoptionRequest a " +
	           "WHERE a.user.userId = :userId " +
	           "AND a.animal.animalId = :animalId " +
	           "AND a.status IN :statuses")
	boolean checkDuplicateRequests(@Param("userId") Long userId,
									@Param("animalId") Long animalId,
									@Param("statuses") List<String> statuses);
	
	
	List<AdoptionRequest> findByUser_UserIdOrderByRequestIdDesc(Long userId);
	
}