package com.example.sajhaKrishi.DTO.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {

    private Long farmerId;
    private Long buyerId;

}