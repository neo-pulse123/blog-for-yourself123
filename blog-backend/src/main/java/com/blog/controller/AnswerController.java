package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.AnswerForm;
import com.blog.entity.Answer;
import com.blog.security.JwtUtils;
import com.blog.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Tag(name = "回答管理")
public class AnswerController {

    private final AnswerService answerService;
    private final JwtUtils jwtUtils;

    @GetMapping("/question/{questionId}")
    @Operation(summary = "获取问题的回答列表")
    public Result<List<Answer>> getAnswersByQuestionId(@PathVariable Long questionId) {
        List<Answer> answers = answerService.getAnswersByQuestionId(questionId);
        return Result.success(answers);
    }

    @PostMapping
    @Operation(summary = "创建回答")
    public Result<Answer> createAnswer(
            @RequestParam Long questionId,
            @Valid @RequestBody AnswerForm form,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Answer answer = answerService.createAnswer(userId, questionId, form);
        return Result.success("回答成功", answer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新回答")
    public Result<Answer> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerForm form,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Answer answer = answerService.updateAnswer(userId, id, form);
        return Result.success("更新成功", answer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除回答")
    public Result<Void> deleteAnswer(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        answerService.deleteAnswer(userId, id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞回答")
    public Result<Void> likeAnswer(@PathVariable Long id) {
        answerService.likeAnswer(id);
        return Result.success("点赞成功");
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "采纳回答")
    public Result<Void> acceptAnswer(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        answerService.acceptAnswer(userId, id);
        return Result.success("采纳成功");
    }
}
