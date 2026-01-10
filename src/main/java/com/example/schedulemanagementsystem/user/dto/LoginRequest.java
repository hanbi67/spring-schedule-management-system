package com.example.schedulemanagementsystem.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank(message = "이메일(email)은 필수입니다.")
    @Email
    private String email;

    @NotBlank(message = "비밀번호(password)는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8글자 이상이어야합니다.")
    private String password;
}
