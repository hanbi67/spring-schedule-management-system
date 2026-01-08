package com.example.schedulemanagementsystem.schedule.service;

import com.example.schedulemanagementsystem.schedule.dto.*;
import com.example.schedulemanagementsystem.schedule.entity.Schedule;
import com.example.schedulemanagementsystem.schedule.repository.ScheduleRepository;
import com.example.schedulemanagementsystem.user.entity.User;
import com.example.schedulemanagementsystem.user.repository.UserRepository;
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

    //일정 생성
    @Transactional
    public CreateScheduleResponse save(Long userId, CreateScheduleRequest request) {
        //userId 가져오기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("없는 일정입니다.")
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
                () -> new IllegalStateException("없는 일정입니다.")
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
                () -> new IllegalStateException("없는 일정입니다.")
        );
        return new GetScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }

    //일정 수정
    @Transactional
    public UpdateScheduleResponse update(Long scheduleId, UpdateScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalStateException("없는 일정입니다.")
        );
        schedule.update(schedule.getTitle(), request.getContent());

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

    //일정 삭제
    @Transactional
    public void delete(Long scheduleId) {
        boolean exists = scheduleRepository.existsById(scheduleId);
        if (!exists) {
            throw new IllegalStateException("없는 일정입니다.");
        }
        scheduleRepository.deleteById(scheduleId);
    }
}
