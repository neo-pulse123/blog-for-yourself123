package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.dto.QuestionForm;
import com.blog.entity.Question;
import com.blog.security.JwtUtils;
import com.blog.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "问题管理")
public class QuestionController {

    private final QuestionService questionService;
    private final JwtUtils jwtUtils;

    @GetMapping
    @Operation(summary = "获取问题列表")
    public Result<IPage<Question>> getQuestionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Page<Question> pageParam = new Page<>(page, pageSize);
        IPage<Question> result = questionService.getQuestionPage(pageParam, keyword, null);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取问题详情")
    public Result<Question> getQuestionDetail(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        questionService.incrementViewCount(id);
        return Result.success(question);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的问题")
    public Result<IPage<Question>> getMyQuestions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Page<Question> pageParam = new Page<>(page, pageSize);
        IPage<Question> result = questionService.getMyQuestions(pageParam, userId);
        return Result.success(result);
    }

    @PostMapping
    @Operation(summary = "创建问题")
    public Result<Question> createQuestion(
            @Valid @RequestBody QuestionForm form,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Question question = questionService.createQuestion(userId, form);
        return Result.success("发布成功", question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新问题")
    public Result<Question> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionForm form,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Question question = questionService.updateQuestion(userId, id, form);
        return Result.success("更新成功", question);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除问题")
    public Result<Void> deleteQuestion(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        questionService.deleteQuestion(userId, id);
        return Result.success("删除成功");
    }
}
