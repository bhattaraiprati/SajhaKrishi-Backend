package com.example.sajhaKrishi.Controller.serviceController;

import com.example.sajhaKrishi.DTO.chat.MessageRequest;
import com.example.sajhaKrishi.Model.Message;
import com.example.sajhaKrishi.Model.Room;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.repository.RoomRepository;
import com.example.sajhaKrishi.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final RoomRepository roomRepository;
    private final UserRepo userRepo;

    public ChatController(RoomRepository roomRepository, UserRepo userRepo) {
        this.roomRepository = roomRepository;
        this.userRepo = userRepo;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        logger.info("Broadcasting message: {}", message);
        return message;
    }

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    @Transactional // Ensure Hibernate session is active
    public Message sendMessage(
            @DestinationVariable Long roomId,
            @Payload MessageRequest request
    ) {
        logger.info("Processing message for roomId: {}, request: {}", roomId, request);

        // Validate room
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            logger.error("Room not found with id: {}", roomId);
            throw new RuntimeException("Room not found with id: " + roomId);
        }
        Room room = roomOptional.get();

        // Validate sender
        User sender = userRepo.findById(request.getSenderId())
                .orElseThrow(() -> {
                    logger.error("Sender not found with id: {}", request.getSenderId());
                    return new RuntimeException("Sender not found");
                });

        // Validate receiver
        User receiver = userRepo.findById(request.getReceiverId())
                .orElseThrow(() -> {
                    logger.error("Receiver not found with id: {}", request.getReceiverId());
                    return new RuntimeException("Receiver not found");
                });

        // Create and populate message
        Message message = new Message();
        message.setContent(request.getContent());
        message.setReceiver(receiver);
        message.setSender(sender);
        message.setTimestamp(LocalDateTime.now());
        message.setRoom(room);

        // Update room (within transaction)
        room.getMessages().add(message);
        room.setLastMessage(message);
        room.setLastActivity(LocalDateTime.now());

        // Persist changes
        roomRepository.save(room);

        logger.info("Message sent successfully to room: {}", roomId);
        return message;
    }
}
