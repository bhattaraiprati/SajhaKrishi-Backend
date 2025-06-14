package com.example.sajhaKrishi.Controller.serviceController;

import com.example.sajhaKrishi.DTO.Chat.MessageRequest;
import com.example.sajhaKrishi.Model.Message;
import com.example.sajhaKrishi.Model.MessageType;
import com.example.sajhaKrishi.Model.Room;
import com.example.sajhaKrishi.Model.User;
import com.example.sajhaKrishi.repository.RoomRepository;
import com.example.sajhaKrishi.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {


    private RoomRepository roomRepository;
    private UserRepo userRepo;

    public ChatController(RoomRepository roomRepository, UserRepo userRepo){
        this.roomRepository = roomRepository;
        this.userRepo = userRepo;
    }

    // for sending and receiving message

    @MessageMapping("/sendMessage/{roomId}") // /app/senMessage/roomId
    @SendTo("/topic/room/{roomId}") // subscribe
    public Message sendMessage(
            @DestinationVariable Long roomId,
            @Payload MessageRequest request
            ) throws Exception {

        Optional<Room> roomOptional = roomRepository.findById(roomId);
        Room room = roomOptional.get();

        User sender = userRepo.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepo.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (room == null) {
            throw new RuntimeException("Room not found with id: " + roomId);
        }

        Message message = new Message();
        message.setContent(request.getContent());
        message.setReceiver(receiver);
        message.setSender(sender);
        message.setTimestamp(LocalDateTime.now());
        message.setRoom(room); // Set the room reference

        // Add message to room
        room.getMessages().add(message);
        room.setLastMessage(message);
        room.setLastActivity(LocalDateTime.now());

        return message;


    }
//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;

//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload Message chatMessage, Principal principal) {
//        try {
//            // Set message metadata
//            chatMessage.setId(UUID.randomUUID().toString());
//            chatMessage.setTimestamp(LocalDateTime.now());
//            chatMessage.setSenderName(principal.getName());
//            chatMessage.setType(MessageType.CHAT);
//
//            log.info("Sending message from {} to {}: {}",
//                    chatMessage.getSenderName(),
//                    chatMessage.getReceiverName(),
//                    chatMessage.getContent());
//
//            // Send to specific user
//            simpMessagingTemplate.convertAndSendToUser(
//                    chatMessage.getReceiverName(),
//                    "/queue/messages",
//                    chatMessage
//            );
//
//            Message deliveryConfirmation = Message.builder()
//                    .id(chatMessage.getId())
//                    .type(MessageType.DELIVERED)
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            simpMessagingTemplate.convertAndSendToUser(
//                    chatMessage.getSenderName(),
//                    "/queue/messages",
//                    deliveryConfirmation
//            );
//
//        } catch (Exception e) {
//            log.error("Error sending message: ", e);
//        }
//    }
//    @MessageMapping("/message")
//    @SendTo("/chatroom/public")
//    public Message receivePublicMessage(@Payload Message message){
//
//
//        return message;
//    }

//    @MessageMapping("/chat.addUser")
//    public void addUser(@Payload Message chatMessage, Principal principal) {
//        try {
//            chatMessage.setType(MessageType.JOIN);
//            chatMessage.setSenderName(principal.getName());
//            chatMessage.setTimestamp(LocalDateTime.now());
//
//            log.info("User joined: {}", principal.getName());
//
//            // Broadcast to all users in public chatroom
//            simpMessagingTemplate.convertAndSend("/topic/public", chatMessage);
//
//        } catch (Exception e) {
//            log.error("Error adding user: ", e);
//        }
//    }

//    @MessageMapping("/private-message")
//    public Message receiverPrivateMessage(@Payload Message message){
//        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private",message);
//        return message;
//    }

//    @MessageMapping("/chat.typing")
//    public void userTyping(@Payload Message chatMessage, Principal principal) {
//        chatMessage.setType(MessageType.TYPING);
//        chatMessage.setSenderName(principal.getName());
//        chatMessage.setTimestamp(LocalDateTime.now());
//
//        simpMessagingTemplate.convertAndSendToUser(
//                chatMessage.getReceiverName(),
//                "/queue/typing",
//                chatMessage
//        );
//    }
//
//    @MessageMapping("/chat.markAsRead")
//    public void markAsRead(@Payload Message chatMessage, Principal principal) {
//        chatMessage.setType(MessageType.READ);
//        chatMessage.setTimestamp(LocalDateTime.now());
//
//        simpMessagingTemplate.convertAndSendToUser(
//                chatMessage.getSenderName(),
//                "/queue/messages",
//                chatMessage
//        );
//    }

}
