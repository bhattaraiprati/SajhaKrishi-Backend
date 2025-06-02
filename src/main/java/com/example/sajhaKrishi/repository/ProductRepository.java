package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.Model.farmer.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByUser(User user);
    List<Product> findByAvailable(Boolean available);

}
