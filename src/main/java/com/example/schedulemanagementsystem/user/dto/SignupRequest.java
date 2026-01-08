package com.example.schedulemanagementsystem.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequest {
    private String name;
    private String email;

    @Size(min = 8, message = "비밀번호는 8글자 이상이어야합니다.")
    private String password;
}
