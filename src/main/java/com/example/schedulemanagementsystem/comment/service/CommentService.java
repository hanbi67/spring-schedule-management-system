package com.example.schedulemanagementsystem.comment.service;

import com.example.schedulemanagementsystem.comment.dto.CreateCommentRequest;
import com.example.schedulemanagementsystem.comment.dto.CreateCommentResponse;
import com.example.schedulemanagementsystem.comment.dto.GetCommentResponse;
import com.example.schedulemanagementsystem.comment.entity.Comment;
import com.example.schedulemanagementsystem.comment.repository.CommentRepository;
import com.example.schedulemanagementsystem.common.exception.NotFoundException;
import com.example.schedulemanagementsystem.schedule.entity.Schedule;
import com.example.schedulemanagementsystem.schedule.repository.ScheduleRepository;
import com.example.schedulemanagementsystem.schedule.service.ScheduleService;
import com.example.schedulemanagementsystem.user.entity.User;
import com.example.schedulemanagementsystem.user.repository.UserRepository;
import com.example.schedulemanagementsystem.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    //댓글 생성 - 로그인 시에만 가능, 본인이 생성한 일정이 아니어도 댓글 저장 가능.
    @Transactional
    public CreateCommentResponse save(Long loginUserId, Long scheduleId, CreateCommentRequest request) {
        //loginUserId(세션 로그인 유저) 찾아오기
        User user = userRepository.findById(loginUserId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다.")
        );

        //ScheduleId 찾아오기
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 일정입니다.")
        );

        //Comment 생성
        Comment Comment = new Comment(request.getContent(), user, schedule);
        Comment savedComment = commentRepository.save(Comment);

        return new CreateCommentResponse(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getCreatedAt(),
                savedComment.getModifiedAt()
        );
    }

    //댓글 전체 조회 - 공개
    @Transactional(readOnly = true)
    public List<GetCommentResponse> findAll(Long scheduleId) {
        //scheduleId로 댓글을 조회하고 댓글 생성 순으로 정렬하고싶다.
        List<Comment> comments = commentRepository.findByScheduleIdOrderByCreatedAtDesc(scheduleId);
        List<GetCommentResponse> dtos = new ArrayList<>();

        for (Comment comment : comments){
            dtos.add(new GetCommentResponse(
                    comment.getId(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getModifiedAt()
            ));
        }
        return dtos;
    }
}
