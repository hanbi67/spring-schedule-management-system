package com.example.schedulemanagementsystem.user.controller;

import com.example.schedulemanagementsystem.user.dto.*;
import com.example.schedulemanagementsystem.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    //로그인 세션 키
    private static final String SESSION_KEY = "loginUser";

    private final UserService userService;

    //유저 생성(회원가입)
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signUp(
            @Valid @RequestBody SignupRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
    }

//    //유저 생성(회원가입)
//    @PostMapping("/users")
//    public ResponseEntity<CreateUserResponse> createUser(
//            @RequestBody CreateUserRequest request
//    ){
//        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
//    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ){
        SessionUser sessionUser = userService.login(request);
        session.setAttribute("loginUser", sessionUser);

        LoginResponse response = new LoginResponse(sessionUser.getId(), sessionUser.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(response);

        //return ResponseEntity.status(HttpStatus.OK).build();
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
    //세션 활용(로그인 상태 + 본인만 수정 가능)
    @PatchMapping("/users/{userId}")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody SignupRequest request,
            @SessionAttribute(name = SESSION_KEY, required = false) SessionUser loginUser
    ){
        requireLogin(loginUser);
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(loginUser.getId(), userId, request));
    }


//    //유저 수정 - 유저명,이메일만 부분 수정 가능 -> Patch
//    @PatchMapping("/users/{userId}")
//    public ResponseEntity<UpdateUserResponse> updateUser(
//            @PathVariable Long userId,
//            @RequestBody SignupRequest request
//    ){
//        return ResponseEntity.status(HttpStatus.OK).body(userService.update(userId, request));
//    }

    //유저 삭제
    //세션 활용(로그인 상태 + 본인만 수정 가능)
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            @SessionAttribute(name = SESSION_KEY, required = false) SessionUser loginUser
    ){
        requireLogin(loginUser);
        userService.delete(loginUser.getId(), userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

//    //유저 삭제
//    @DeleteMapping("/users/{userId}")
//    public ResponseEntity<Void> deleteUser(
//            @PathVariable Long userId
//    ){
//        userService.delete(userId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

    //로그인 여부 확인
    private void requireLogin(SessionUser loginUser) {
        if (loginUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
    }

}
