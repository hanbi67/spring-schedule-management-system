package com.example.schedulemanagementsystem.schedule.controller;

import com.example.schedulemanagementsystem.common.exception.UnauthorizedException;
import com.example.schedulemanagementsystem.schedule.dto.*;
import com.example.schedulemanagementsystem.schedule.service.ScheduleService;
import com.example.schedulemanagementsystem.user.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    //로그인 세션 키
    private static final String SESSION_KEY = "loginUser";

    private final ScheduleService scheduleService;

    //일정 생성 (로그인 + 본인 일정만 생성 가능)
    @PostMapping("/users/{userId}/schedules")
    public ResponseEntity<CreateScheduleResponse> createSchedule(
            @PathVariable Long userId,
            @Valid @RequestBody CreateScheduleRequest request,
            @SessionAttribute(name = SESSION_KEY, required = false) SessionUser loginUser
    ){
        requireLogin(loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.save(loginUser.getId(), userId, request));
    }

    //일정 전체 조회 (공개)
    @GetMapping("/users/{userId}/schedules")
    public ResponseEntity<List<GetScheduleResponse>> getAllSchedules(
            @PathVariable Long userId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findAll(userId));
    }

    //일정 단건 조회 (공개)
    @GetMapping("/users/{userId}/schedules/{scheduleId}")
    public ResponseEntity<GetScheduleResponse> getOneSchedules(
            @PathVariable Long scheduleId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findOne(scheduleId));
    }

    //일정 수정 (로그인 + 본인만 수정 가능)
    @PutMapping("/users/{userId}/schedules/{scheduleId}")
    public ResponseEntity<UpdateScheduleResponse> updateSchedule(
            @PathVariable Long userId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateScheduleRequest request,
            @SessionAttribute(name = SESSION_KEY, required = false) SessionUser loginUser
    ){
        requireLogin(loginUser);
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.update(loginUser.getId(), userId, scheduleId, request));
    }

    //일정 삭제 (로그인 + 본인만 삭제 가능)
    @DeleteMapping("/users/{userId}/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long userId,
            @PathVariable Long scheduleId,
            @SessionAttribute(name = SESSION_KEY, required = false) SessionUser loginUser
    ){
        requireLogin(loginUser);
        scheduleService.delete(loginUser.getId(), userId, scheduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 일정 페이징 조회 (공개)
    @GetMapping("/schedules")
    public ResponseEntity<Page<SchedulePageResponse>> getSchedulesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findPage(page, size));
    }


    //로그인 여부 확인
    private void requireLogin(SessionUser loginUser) {
        if (loginUser == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
    }

}
