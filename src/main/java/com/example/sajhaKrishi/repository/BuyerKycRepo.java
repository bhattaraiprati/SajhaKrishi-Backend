package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.BuyerKyc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuyerKycRepo extends JpaRepository<BuyerKyc, Long> {

    BuyerKyc findByUserId(Long userId);
    BuyerKyc findByEmail(String email);

    // Fetch KYC by status
    List<BuyerKyc> findByKycStatus(String kycStatus);

    // Search by buyer name
    @Query("SELECT bk FROM BuyerKyc bk WHERE bk.fullName LIKE %:name% AND bk.kycStatus = :status")
    List<BuyerKyc> findByFullNameAndStatus(@Param("name") String name, @Param("status") String status);


}
