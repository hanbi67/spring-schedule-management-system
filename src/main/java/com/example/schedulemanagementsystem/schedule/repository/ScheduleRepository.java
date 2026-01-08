package com.example.schedulemanagementsystem.schedule.repository;

import com.example.schedulemanagementsystem.schedule.entity.Schedule;
import com.example.schedulemanagementsystem.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUser(User user);
}
