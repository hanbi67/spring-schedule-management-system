package com.example.schedulemanagementsystem.schedule.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SchedulePageResponse {
    //일정 페이징 조회용 dto
    private final String title;
    private final String content;
    private final Long commentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final String userName;

    public SchedulePageResponse(String title, String content, Long commentCount, LocalDateTime createdAt, LocalDateTime modifiedAt, String userName) {
        this.title = title;
        this.content = content;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.userName = userName;
    }
}
