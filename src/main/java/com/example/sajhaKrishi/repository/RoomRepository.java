package com.example.sajhaKrishi.repository;

import com.example.sajhaKrishi.Model.Room;
import com.example.sajhaKrishi.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByFarmerId(Long farmerId);
    Room findByBuyerId(Long buyerId);
    Room findByFarmer(User id);
    List<Room> findByFarmerOrBuyer(User user1, User user2);

    Room findByFarmerAndBuyer(User farmer, User buyer);
}
