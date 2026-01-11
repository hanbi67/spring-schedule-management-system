package com.example.schedulemanagementsystem.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateScheduleRequest {

    @NotBlank(message = "일정 제목(title)은 필수입니다.")
    @Size(max = 10, message = "일정 제목은 10글자까지 입니다.")
    private String title;

    private String content;
}
