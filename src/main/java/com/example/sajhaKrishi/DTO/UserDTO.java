package com.example.sajhaKrishi.DTO;

import com.example.sajhaKrishi.repository.UserRepo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    public UserDTO(Long id, String name, String email, Long number, String role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
        this.role = role;
    }

    private Long id;
    private String name;
    private String email;
    private Long number;
    private String role;
}
