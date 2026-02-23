package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dto.NoteForm;
import com.blog.entity.Category;
import com.blog.entity.Note;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.mapper.NoteMapper;
import com.blog.service.CategoryService;
import com.blog.service.NoteService;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    public IPage<Note> getNotePage(Page<Note> page, Long categoryId, String keyword, Long userId) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getStatus, 1);
        
        if (categoryId != null) {
            wrapper.eq(Note::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Note::getTitle, keyword);
        }
        if (userId != null) {
            wrapper.eq(Note::getUserId, userId);
        }
        
        wrapper.orderByDesc(Note::getCreateTime);
        
        IPage<Note> result = page(page, wrapper);
        
        result.getRecords().forEach(note -> {
            User user = userService.getById(note.getUserId());
            if (user != null) {
                note.setUser(user);
            }
            if (note.getCategoryId() != null) {
                Category category = categoryService.getById(note.getCategoryId());
                if (category != null) {
                    note.setCategory(category);
                }
            }
        });
        
        return result;
    }

    @Override
    public Note getNoteById(Long id) {
        Note note = getById(id);
        if (note == null || note.getStatus() == 3) {
            throw new BusinessException("笔记不存在");
        }
        
        User user = userService.getById(note.getUserId());
        if (user != null) {
            note.setUser(user);
        }
        if (note.getCategoryId() != null) {
            Category category = categoryService.getById(note.getCategoryId());
            if (category != null) {
                note.setCategory(category);
            }
        }
        
        return note;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Note createNote(Long userId, NoteForm form) {
        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(form.getTitle());
        note.setContent(form.getContent());
        note.setSummary(form.getSummary());
        note.setCategoryId(form.getCategoryId());
        note.setViewCount(0);
        note.setLikeCount(0);
        note.setStatus(1);
        
        save(note);
        log.info("用户 {} 创建笔记: {}", userId, note.getTitle());
        
        return note;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Note updateNote(Long userId, Long id, NoteForm form) {
        Note note = getById(id);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此笔记");
        }
        
        note.setTitle(form.getTitle());
        note.setContent(form.getContent());
        note.setSummary(form.getSummary());
        note.setCategoryId(form.getCategoryId());
        
        updateById(note);
        log.info("用户 {} 更新笔记: {}", userId, note.getTitle());
        
        return note;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNote(Long userId, Long id) {
        Note note = getById(id);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此笔记");
        }
        
        note.setStatus(3);
        updateById(note);
        log.info("用户 {} 删除笔记: {}", userId, id);
    }

    @Override
    public void incrementViewCount(Long id) {
        Note note = getById(id);
        if (note != null) {
            note.setViewCount(note.getViewCount() + 1);
            updateById(note);
        }
    }

    @Override
    public void incrementLikeCount(Long id) {
        Note note = getById(id);
        if (note != null) {
            note.setLikeCount(note.getLikeCount() + 1);
            updateById(note);
        }
    }

    @Override
    public IPage<Note> getMyNotes(Page<Note> page, Long userId) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, userId);
        wrapper.ne(Note::getStatus, 3);
        wrapper.orderByDesc(Note::getCreateTime);
        
        return page(page, wrapper);
    }
}
