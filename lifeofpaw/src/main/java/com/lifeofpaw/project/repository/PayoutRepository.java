package com.lifeofpaw.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lifeofpaw.project.entity.Payout;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long>{
	List<Payout> findByOrganization_OrgId(Long orgId);
	
	@Query("SELECT SUM(p.amount) FROM Payout p")
	Double getTotalDistributedFunds();
	
	@Query("SELECT p FROM Payout p ORDER BY p.payoutDate DESC")
	List<Payout> findAllOrderByPayoutDateDesc();
}
