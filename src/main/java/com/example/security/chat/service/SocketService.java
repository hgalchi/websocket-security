package com.example.security.chat.service;

import com.example.security.Entity.User;
import com.example.security.chat.Entity.Chatmessage;
import com.example.security.chat.Entity.Chatroom;
import com.example.security.chat.Entity.UserChatroom;
import com.example.security.chat.repository.ChatmessageRepository;
import com.example.security.chat.repository.ChatroomRepository;
import com.example.security.chat.repository.UserChatroomRepository;
import com.example.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class SocketService {


    private final ChatroomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserChatroomRepository userChatroomRepository;
    private final ChatmessageRepository chatMessageRepository;

    /**
     * 채팅방 입장
     * @param email 유저 email
     * @param roomId 입장하는 room pk
     */
    @Transactional
    public void join(String email,Long roomId) {
        Chatroom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new UsernameNotFoundException("채팅방을 찾을 수 없습니다."));
        //현재 인원수 확인
        if ( chatRoom.getMaxCount()> chatRoom.getCount()) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
            //채팅방 입장
            UserChatroom userChatroom = UserChatroom.builder()
                    .chatRoom(chatRoom)
                    .user(user)
                    .build();

            chatRoom.setUserCount(chatRoom.getCount()+1);
            chatRoom.addDetail(userChatroom);
            chatRoomRepository.save(chatRoom);
        }

    }

    /**
     * 채팅방 퇴방
     * @param email 유저 email
     * @param roomId 퇴장하는 room pk
     */
    @Transactional
    public void leave(String email,Long roomId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        Chatroom chatroom = chatRoomRepository.findById(roomId).orElseThrow(() -> new UsernameNotFoundException("채팅방을 찾을 수 없습니다."));
        UserChatroom userChatroom = userChatroomRepository.findByUserAndChatRoom(user, chatroom).orElseThrow(() -> new UsernameNotFoundException("채팅방에 해당 회원이 존재하지 않습니다."));
        chatroom.removeDetail(userChatroom);
        chatroom.setUserCount(chatroom.getCount()-1);
        chatRoomRepository.save(chatroom);
    }

    /**
     * 채팅 메세지 저장
     * @param message 유저가 보낸 메세지
     * @param roomId room pk
     * @param email 유저 email
     */
    public void saveMessage(String message, Long roomId,String email) {
        Chatroom chatroom = chatRoomRepository.findById(roomId).get();
        Chatmessage chatmessage = Chatmessage.builder()
                .message(message)
                .chatRoom(chatroom)
                .message(message)
                .sender(email)
                .time(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatmessage);
    }
}
