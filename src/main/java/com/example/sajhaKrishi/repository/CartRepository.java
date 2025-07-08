package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.buyer.CartItem;
import com.example.sajhaKrishi.Model.buyer.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    List<CartItem> findByUserIdAndStatus(Long userId, CartStatus status);

    @Query("SELECT c FROM CartItem c WHERE c.userId = :userId AND c.status = :status")
    List<CartItem> findByUserIdAndStatusCustom(@Param("userId") Long userId,
                                               @Param("status") CartStatus status);

    @Query("SELECT SUM(c.price * c.quantity) FROM CartItem c WHERE c.userId = :userId AND c.status = :status")
    Double calculateTotalByUserIdAndStatus(@Param("userId") Long userId,
                                           @Param("status") CartStatus status);

    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.userId = :userId AND c.status = :status")
    Integer countItemsByUserIdAndStatus(@Param("userId") Long userId,
                                        @Param("status") CartStatus status);

}
