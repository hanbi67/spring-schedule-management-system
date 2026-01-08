package com.example.schedulemanagementsystem.user.dto;

import lombok.Getter;

@Getter
public class SessionUser {
    private final Long id;
    private final String name;
    private final String email;

    public SessionUser(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
