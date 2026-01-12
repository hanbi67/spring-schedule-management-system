package com.example.schedulemanagementsystem.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateCommentRequest {
    @NotBlank(message = "댓글 내용(content)은 필수입니다.")
    @Size(min = 1, message = "댓글 내용은 최소 1글자입니다.")
    private String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }
}
