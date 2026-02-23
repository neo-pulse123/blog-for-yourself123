package com.blog.service;

import com.blog.entity.Note;
import com.blog.entity.Question;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public interface CacheService {

    @Cacheable(value = "notes", key = "'page:' + #page + ':' + #pageSize + ':' + #categoryId + ':' + #keyword")
    IPage<Note> getNotePage(Page<Note> page, Long categoryId, String keyword);

    @Cacheable(value = "notes", key = "#id")
    Note getNoteById(Long id);

    @CacheEvict(value = "notes", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    Note createNote(Long userId, Object form);

    @CacheEvict(value = "notes", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    Note updateNote(Long userId, Long id, Object form);

    @CacheEvict(value = "notes", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteNote(Long userId, Long id);

    @Cacheable(value = "questions", key = "'page:' + #page + ':' + #pageSize + ':' + #keyword")
    IPage<Question> getQuestionPage(Page<Question> page, String keyword);

    @Cacheable(value = "questions", key = "#id")
    Question getQuestionById(Long id);

    @CacheEvict(value = "questions", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    Question createQuestion(Long userId, Object form);

    @CacheEvict(value = "questions", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    void deleteQuestion(Long userId, Long id);

    void clearAllCaches();
}
