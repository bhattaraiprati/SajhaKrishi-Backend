package com.example.sajhaKrishi.Controller.serviceController;

import com.example.sajhaKrishi.DTO.Chat.CreateRoomRequest;
import com.example.sajhaKrishi.Model.Message;
import com.example.sajhaKrishi.Model.Room;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.repository.RoomRepository;
import com.example.sajhaKrishi.repository.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin("http://localhost:5173")
public class RoomController {

    private RoomRepository roomRepository;
    private UserRepo userRepo;
    public RoomController(RoomRepository roomRepository, UserRepo userRepo){
        this.roomRepository = roomRepository;
        this.userRepo = userRepo;
    }

    @PostMapping("/createRoom")
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomRequest request){

        // Validate input
        if (request.getFarmerId() == null || request.getBuyerId() == null) {
            throw new IllegalArgumentException("Farmer ID and Buyer ID must not be null");
        }
        // Fetch users from repository
        User farmer = userRepo.findById(request.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + request.getFarmerId()));

        User buyer = userRepo.findById(request.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found with id: " + request.getBuyerId()));

        Room roomRepo = roomRepository.findByFarmerAndBuyer(farmer, buyer);
        if (roomRepo != null) {
            return ResponseEntity.ok(roomRepo);
        }

        Room room = new Room();
        room.setFarmer(farmer);
        room.setBuyer(buyer);
        room.setCreatedAt(LocalDateTime.now());

        Room savedRoom = roomRepository.save(room);
        return ResponseEntity.ok(savedRoom);

    }

    @GetMapping("/getUserRoom/{id}")
    public ResponseEntity<?> getRoomDetails(@PathVariable User id){

        Room userRoom = roomRepository.findByFarmer(id);

        if (userRoom == null){
            return  ResponseEntity.ok("Empty Room.");

        }

        return ResponseEntity.ok(userRoom);
    }


    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "20", required = false) int size
    ){
        Optional<Room> roomOptional = roomRepository.findById(id);
        if (roomOptional.isEmpty()){
            return  ResponseEntity.badRequest().build();
        }

        Room room = roomOptional.get();
        List<Message> messages = room.getMessages();

        int start = Math.max(0, messages.size()- (page+1) * size);
        int end = Math.min(messages.size(), start + size);

        List<Message> paginatedMessage = messages.subList(start, end);

        return ResponseEntity.ok(paginatedMessage);

    }
}
