package com.example.schedulemanagementsystem.user.controller;

import com.example.schedulemanagementsystem.user.dto.CreateUserRequest;
import com.example.schedulemanagementsystem.user.dto.CreateUserResponse;
import com.example.schedulemanagementsystem.user.dto.GetUserResponse;
import com.example.schedulemanagementsystem.user.dto.UpdateUserResponse;
import com.example.schedulemanagementsystem.user.entity.User;
import com.example.schedulemanagementsystem.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //유저 생성(회원가입)
    @PostMapping("/users")
    public ResponseEntity<CreateUserResponse> createUser(
            @RequestBody CreateUserRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
    }

    //유저 전체 조회
    @GetMapping("/users")
    public ResponseEntity<List<GetUserResponse>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    //유저 단건 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<GetUserResponse> getOneUser(
            @PathVariable Long userId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findOne(userId));
    }

    //유저 수정 - 유저명,이메일만 부분 수정 가능 -> Patch
    @PatchMapping("/users/{userId}")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody CreateUserRequest request
    ){
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(userId, request));
    }

    //유저 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId
    ){
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
