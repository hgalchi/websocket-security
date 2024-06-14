package com.example.security.chat.controller;

import com.example.security.chat.Entity.Chatroom;
import com.example.security.chat.repository.ChatroomRepository;
import com.example.security.chat.service.SocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatroomRepository chatRoomRepository;
    private final SocketService socketService;

    //채팅방 리스트 로드
    @GetMapping("/list")
    public String list() {
        List<Chatroom> list = chatRoomRepository.findAll();
        return list.toString();
    }

    //채팅방 생성
    @PostMapping("/create")
    public void create(String name, @RequestParam(required = true,defaultValue = "5") int maxCount) {
        Chatroom chatroom = Chatroom.builder()
                .name(name)
                .count(0)
                .maxCount(maxCount)
                .build();
        chatRoomRepository.save(chatroom);
    }

}
