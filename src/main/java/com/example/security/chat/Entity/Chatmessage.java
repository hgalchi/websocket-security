package com.example.security.chat.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Chatmessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;

    private String sender;

    //저장 형식 수정
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "CHATROOM_ID")
    private Chatroom chatRoom;
}
