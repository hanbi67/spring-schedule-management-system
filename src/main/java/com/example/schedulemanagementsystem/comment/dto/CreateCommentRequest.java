package com.example.schedulemanagementsystem.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateCommentRequest {
    @NotBlank(message = "댓글 내용(content)은 필수입니다.")
    private String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }
}
