package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.order.Order;
import com.example.sajhaKrishi.Model.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTransactionUuid(String transactionUuid);
    List<Order> findByFarmerId(Long userId);

    List<Order> findByUserId(Long id);

    // Add these new methods for filtering
    List<Order> findByFarmerIdAndOrderStatus(Long farmerId, OrderStatus orderStatus);
    List<Order> findByFarmerIdAndCreatedAtBetween(Long farmerId, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByFarmerIdAndDeliveryInfoFullNameContainingIgnoreCase(Long farmerId, String name);
    List<Order> findByFarmerIdAndId(Long farmerId, Long orderId);
}
