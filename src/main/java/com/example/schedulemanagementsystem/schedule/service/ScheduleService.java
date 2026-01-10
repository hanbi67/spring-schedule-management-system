package com.example.schedulemanagementsystem.schedule.service;

import com.example.schedulemanagementsystem.common.exception.ForbiddenException;
import com.example.schedulemanagementsystem.common.exception.NotFoundException;
import com.example.schedulemanagementsystem.schedule.dto.*;
import com.example.schedulemanagementsystem.schedule.entity.Schedule;
import com.example.schedulemanagementsystem.schedule.repository.ScheduleRepository;
import com.example.schedulemanagementsystem.user.entity.User;
import com.example.schedulemanagementsystem.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    //로그인한 유저와 path userId가 같을 때만
    //일정 생성
    @Transactional
    public CreateScheduleResponse save(Long loginUserId, Long userId, @Valid CreateScheduleRequest request) {
        //본인 확인
        if (!loginUserId.equals(userId)) {
            throw new ForbiddenException("본인 일정만 생성할 수 있습니다.");
        }

        //userId 가져오기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다.")
        );

        //Schedule 생성
        Schedule schedule = new Schedule(request.getTitle(), request.getContent(), user);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return new CreateScheduleResponse(
                savedSchedule.getId(),
                savedSchedule.getTitle(),
                savedSchedule.getContent(),
                savedSchedule.getCreatedAt(),
                savedSchedule.getModifiedAt()
        );
    }

    //일정 전체 조회
    @Transactional(readOnly = true)
    public List<GetScheduleResponse> findAll(Long userId) {
        //userId 가져오기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다.")
        );

        List<Schedule> schedules = scheduleRepository.findByUser(user);
        List<GetScheduleResponse> dtos = new ArrayList<>();
        for (Schedule schedule : schedules) {
            dtos.add(new GetScheduleResponse(
                    schedule.getId(),
                    schedule.getTitle(),
                    schedule.getContent(),
                    schedule.getCreatedAt(),
                    schedule.getModifiedAt())
            );
        }
        return dtos;
    }

    //일정 단건 조회
    @Transactional(readOnly = true)
    public GetScheduleResponse findOne(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ForbiddenException("없는 일정입니다.")
        );
        return new GetScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    //로그인한 유저와 path userId가 같을 때만
    //일정 수정
    @Transactional
    public UpdateScheduleResponse update(Long loginUserId, Long userId, Long scheduleId, @Valid UpdateScheduleRequest request) {
        //본인 확인
        if (!loginUserId.equals(userId)) {
            throw new ForbiddenException("본인 일정만 수정할 수 있습니다.");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new NotFoundException("없는 일정입니다.")
        );

        //내가 작성한 일정인지 확인
        if (!schedule.getUser().getId().equals(loginUserId)) {
            throw new ForbiddenException("본인 일정만 수정할 수 있습니다.");
        }

        schedule.update(request.getTitle(), request.getContent());
        //flush
        scheduleRepository.flush();

        return new UpdateScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    //로그인한 유저와 path userId가 같을 때만
    //일정 삭제
    @Transactional
    public void delete(Long loginUserId, Long userId, Long scheduleId) {
        //본인 확인
        if (!loginUserId.equals(userId)) {
            throw new ForbiddenException("본인 일정만 삭제할 수 있습니다.");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new NotFoundException("없는 일정입니다.")
        );

        //내가 작성한 일정인지 확인
        if (!schedule.getUser().getId().equals(loginUserId)) {
            throw new ForbiddenException("본인 일정만 삭제할 수 있습니다.");
        }

        scheduleRepository.delete(schedule);
    }
}
