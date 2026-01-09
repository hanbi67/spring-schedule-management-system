package com.example.schedulemanagementsystem.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateUserRequest {
    @NotBlank(message = "이름(name)은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일(email)은 필수입니다.")
    private String email;
}
