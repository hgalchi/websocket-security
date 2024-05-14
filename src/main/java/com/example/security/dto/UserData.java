package com.example.security.dto;

import com.example.security.Entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class UserData {

    private String name;
    private String password;
    private String email;
    private String token;

    public UserEntity toUserEntity(){
        return UserEntity.builder().name(name).password(password).email(email).build();
    }

}
