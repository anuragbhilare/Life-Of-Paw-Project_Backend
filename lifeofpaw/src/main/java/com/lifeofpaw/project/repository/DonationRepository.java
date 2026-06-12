package com.lifeofpaw.project.repository;

import com.lifeofpaw.project.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long>{

	@Query("SELECT SUM(d.amount) FROM Donation d WHERE d.status = 'SUCCESS'")
	Double getTotalPlatformFunds();

	List<Donation> findByDonor_UserId(Long userId);

	@Query("SELECT d FROM Donation d WHERE d.donor.userId = :userId ORDER BY d.transactionDate DESC")
	List<Donation> findByUser_UserIdOrderByDateDesc(Long userId);

	@Query("SELECT d FROM Donation d ORDER BY d.transactionDate DESC")
	List<Donation> findAllOrderByTransactionDateDesc();

}
