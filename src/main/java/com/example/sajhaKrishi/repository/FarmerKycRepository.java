package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.farmer.FarmerKyc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerKycRepository extends JpaRepository<FarmerKyc, Long> {
    FarmerKyc findByUserId(Long userId);
    FarmerKyc findByUserEmail(String email);
}
