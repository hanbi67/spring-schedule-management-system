package com.example.schedulemanagementsystem.schedule.repository;

import com.example.schedulemanagementsystem.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
