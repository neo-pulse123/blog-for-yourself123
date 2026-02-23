package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerForm {
    @NotBlank(message = "回答内容不能为空")
    private String content;
}
