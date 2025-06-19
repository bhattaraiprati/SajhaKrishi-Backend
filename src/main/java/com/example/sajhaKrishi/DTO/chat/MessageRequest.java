package com.example.sajhaKrishi.DTO.chat;

import com.example.sajhaKrishi.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    private Long roomId;

    private Long senderId;
    private Long receiverId;
    private String content;

    private LocalDateTime timestamp;
}
