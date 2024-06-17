package com.example.security.dto;

import com.example.security.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserData {

    @Size(min = 3, message = "3~5자리로 입력해주세요")
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @Size(min = 8,max=16, message = "4~8자리로 입력해주세요")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @Email
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    public User toUserEntity(){
        return User.builder().name(name).password(password).email(email).build();
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
