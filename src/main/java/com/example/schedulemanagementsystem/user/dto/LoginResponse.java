package com.example.schedulemanagementsystem.user.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final Long id;
    private final String email;

    public LoginResponse(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
