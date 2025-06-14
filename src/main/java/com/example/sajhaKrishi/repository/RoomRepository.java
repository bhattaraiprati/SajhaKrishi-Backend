package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.Room;
import com.example.sajhaKrishi.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Find all rooms for a user

//    Optional<T> findById(ID id);

    Room findByFarmer(User id);
    List<Room> findByFarmerOrBuyer(User user1, User user2);

    Room findByFarmerAndBuyer(User farmer, User buyer);



}
