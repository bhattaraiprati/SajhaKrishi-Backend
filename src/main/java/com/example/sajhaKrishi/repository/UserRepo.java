package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.DTO.UserDTO;
import com.example.sajhaKrishi.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    User findByName(String name);

    @Query("SELECT new com.example.sajhaKrishi.DTO.UserDTO(u.id, u.name, u.email, u.number, u.role) FROM User u WHERE u.id = :id")
    UserDTO findUserById(@Param("id") Long id);


    User findByEmail(String email);

}
