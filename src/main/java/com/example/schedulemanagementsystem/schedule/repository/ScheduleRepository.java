package com.example.schedulemanagementsystem.schedule.repository;

import com.example.schedulemanagementsystem.schedule.dto.SchedulePageResponse;
import com.example.schedulemanagementsystem.schedule.entity.Schedule;
import com.example.schedulemanagementsystem.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUser(User user);

    @Query("""
            select new com.example.schedulemanagementsystem.schedule.dto.SchedulePageResponse(
                s.title,
                s.content,
                count(c),
                s.createdAt,
                s.modifiedAt,
                u.name
            )
            from Schedule s
            join s.user u
            left join com.example.schedulemanagementsystem.comment.entity.Comment c
                on c.schedule = s
            group by s.id, s.title, s.content, s.createdAt, s.modifiedAt, u.name
            """)
    Page<SchedulePageResponse> findSchedulePage(Pageable pageable);
}
