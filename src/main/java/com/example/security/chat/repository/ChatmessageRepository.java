package com.example.security.chat.repository;


import com.example.security.chat.Entity.Chatmessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatmessageRepository extends JpaRepository<Chatmessage, Long> {

}
