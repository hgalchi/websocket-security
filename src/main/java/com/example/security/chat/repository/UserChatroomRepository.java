package com.example.security.chat.repository;

import com.example.security.Entity.User;
import com.example.security.chat.Entity.Chatroom;
import com.example.security.chat.Entity.UserChatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserChatroomRepository extends JpaRepository<UserChatroom, Long> {

    //JPA엔티티 클래스에 정의된 필드명으로 지정
    public Optional<UserChatroom> findByUserAndChatRoom(User user, Chatroom chatroom);

}
