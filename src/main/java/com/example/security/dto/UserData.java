package com.example.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserData {

    private String name;
    private String password;
    private String email;

}
