package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTransactionUuid(String transactionUuid);
    List<Order> findByFarmerId(Long userId);
}
