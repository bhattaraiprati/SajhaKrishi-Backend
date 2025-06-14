package com.example.sajhaKrishi.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.annotation.Documented;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User farmer;

    @ManyToOne
    private User buyer;

    private LocalDateTime createdAt;

    @OneToOne
    private Message lastMessage;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    private boolean farmerActive;
    private boolean buyerActive;
    private LocalDateTime lastActivity;
}
