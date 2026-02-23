package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.dto.NoteForm;
import com.blog.entity.Note;
import com.blog.entity.Category;
import com.blog.security.JwtUtils;
import com.blog.service.CategoryService;
import com.blog.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "笔记管理")
public class NoteController {

    private final NoteService noteService;
    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;

    @GetMapping
    @Operation(summary = "获取笔记列表")
    public Result<IPage<Note>> getNoteList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        
        Page<Note> pageParam = new Page<>(page, pageSize);
        IPage<Note> result = noteService.getNotePage(pageParam, categoryId, keyword, null);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取笔记详情")
    public Result<Note> getNoteDetail(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        noteService.incrementViewCount(id);
        return Result.success(note);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的笔记")
    public Result<IPage<Note>> getMyNotes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Page<Note> pageParam = new Page<>(page, pageSize);
        IPage<Note> result = noteService.getMyNotes(pageParam, userId);
        return Result.success(result);
    }

    @PostMapping
    @Operation(summary = "创建笔记")
    public Result<Note> createNote(
            @Valid @RequestBody NoteForm form,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Note note = noteService.createNote(userId, form);
        return Result.success("创建成功", note);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新笔记")
    public Result<Note> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteForm form,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Note note = noteService.updateNote(userId, id, form);
        return Result.success("更新成功", note);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除笔记")
    public Result<Void> deleteNote(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        noteService.deleteNote(userId, id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞笔记")
    public Result<Void> likeNote(@PathVariable Long id) {
        noteService.incrementLikeCount(id);
        return Result.success("点赞成功");
    }

    @GetMapping("/categories")
    @Operation(summary = "获取分类列表")
    public Result<List<Category>> getCategories() {
        return Result.success(categoryService.list());
    }
}
