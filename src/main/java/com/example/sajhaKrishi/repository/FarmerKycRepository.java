package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FarmerKycRepository extends JpaRepository<FarmerKyc, Long> {

    FarmerKyc findByUserId(Long userId);
    FarmerKyc findByUserEmail(String email);

    // Fetch KYC by status
    List<FarmerKyc> findByKycStatus(String kycStatus);

    // Search by farmer name (assuming User has a name field)
    @Query("SELECT fk FROM FarmerKyc fk WHERE fk.user.name LIKE %:name% AND fk.kycStatus = :status")
    List<FarmerKyc> findByUserNameAndStatus(@Param("name") String name, @Param("status") String status);
}