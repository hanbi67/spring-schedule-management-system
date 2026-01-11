package com.example.schedulemanagementsystem.comment.controller;

import com.example.schedulemanagementsystem.comment.dto.CreateCommentRequest;
import com.example.schedulemanagementsystem.comment.dto.CreateCommentResponse;
import com.example.schedulemanagementsystem.comment.dto.GetCommentResponse;
import com.example.schedulemanagementsystem.comment.service.CommentService;
import com.example.schedulemanagementsystem.common.exception.UnauthorizedException;
import com.example.schedulemanagementsystem.user.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    //로그인 세션 키
    private static final String SESSION_KEY = "loginUser";

    private final CommentService commentService;

    //댓글 저장 - 로그인 시에만 가능, 본인이 생성한 일정이 아니어도 댓글 저장 가능.
    @PostMapping("/schedules/{scheduleId}/comments")
    public ResponseEntity<CreateCommentResponse> saveComment(
            @PathVariable Long scheduleId,
            @RequestBody CreateCommentRequest request,
            @SessionAttribute(name = SESSION_KEY, required = false) SessionUser loginUser
    ){
        requireLogin(loginUser);

        // 실제 댓글 작성자는 세션 로그인 유저(loginUser)
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(loginUser.getId(), scheduleId, request));
    }

    //댓글 전체 조회 - 공개
    @GetMapping("/schedules/{scheduleId}/comments")
    public ResponseEntity<List<GetCommentResponse>> getAllComments(
            @PathVariable Long scheduleId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findAll(scheduleId));
    }

    //로그인 여부 확인
    private void requireLogin(SessionUser loginUser) {
        if (loginUser == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
    }
}
