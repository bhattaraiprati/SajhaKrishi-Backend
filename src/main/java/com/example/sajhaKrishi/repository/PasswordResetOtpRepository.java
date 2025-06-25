package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    PasswordResetOtp findByUserId(Long id);

    @Query("SELECT o FROM PasswordResetOtp o WHERE o.user.id = :userId AND o.isUsed = false AND o.expiresAt > CURRENT_TIMESTAMP ORDER BY o.createdAt DESC")
    Optional<PasswordResetOtp> findLatestValidByUserId(Long userId);
}
