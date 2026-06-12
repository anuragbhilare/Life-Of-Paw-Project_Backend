package com.lifeofpaw.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifeofpaw.project.entity.Animal;


@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long>{

	List<Animal> findBySpecies(String species);
	List<Animal> findByStatus(String status);
	List<Animal> findByGender(String gender);
	
	List<Animal> findBySpeciesIgnoreCaseAndGenderIgnoreCaseAndStatusIgnoreCase(
			String species,String gender,String status
			);
	
	List<Animal> findByOrganization_orgId(Long orgId);

	
	
	@Query("SELECT COUNT(a) > 0 FROM Animal a WHERE " +
           "UPPER(a.name) = UPPER(:name) AND " +
           "UPPER(a.species) = UPPER(:species) AND " +
           "UPPER(a.gender) = UPPER(:gender) AND " +
           "a.status = :status AND " +
           "a.organization.orgId = :orgId")
	 boolean checkDuplicateAnimal(
		        @Param("name") String name, 
		        @Param("species") String species, 
		        @Param("gender") String gender, 
		        @Param("status") String status, 
		        @Param("orgId") Long orgId
		    );

	List<Animal> findAllByOrganization_OrgId(Long orgId);
	
}
