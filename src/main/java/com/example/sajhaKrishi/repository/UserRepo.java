package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

    User findByName(String name);

    User findByEmail(String email);

}
