package com.blog.service.impl;

import com.blog.entity.Note;
import com.blog.entity.Question;
import com.blog.mapper.NoteMapper;
import com.blog.mapper.QuestionMapper;
import com.blog.service.CacheService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final NoteMapper noteMapper;
    private final QuestionMapper questionMapper;

    @Override
    @Cacheable(value = "notes", key = "'page:' + #page + ':' + #pageSize + ':' + #categoryId + ':' + #keyword", unless = "#result == null")
    public IPage<Note> getNotePage(Page<Note> page, Long categoryId, String keyword) {
        return noteMapper.selectNotePage(page, categoryId, keyword);
    }

    @Override
    @Cacheable(value = "notes", key = "#id", unless = "#result == null")
    public Note getNoteById(Long id) {
        Note note = noteMapper.selectById(id);
        if (note != null) {
            noteMapper.incrementViewCount(id);
        }
        return note;
    }

    @Override
    @CacheEvict(value = "notes", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Note createNote(Long userId, Object form) {
        return null;
    }

    @Override
    @CacheEvict(value = "notes", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Note updateNote(Long userId, Long id, Object form) {
        return null;
    }

    @Override
    @CacheEvict(value = "notes", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteNote(Long userId, Long id) {
        noteMapper.deleteById(id);
    }

    @Override
    @Cacheable(value = "questions", key = "'page:' + #page + ':' + #pageSize + ':' + #keyword", unless = "#result == null")
    public IPage<Question> getQuestionPage(Page<Question> page, String keyword) {
        return questionMapper.selectQuestionPage(page, keyword);
    }

    @Override
    @Cacheable(value = "questions", key = "#id", unless = "#result == null")
    public Question getQuestionById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question != null) {
            questionMapper.incrementViewCount(id);
        }
        return question;
    }

    @Override
    @CacheEvict(value = "questions", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Question createQuestion(Long userId, Object form) {
        return null;
    }

    @Override
    @CacheEvict(value = "questions", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestion(Long userId, Long id) {
        questionMapper.deleteById(id);
    }

    @Override
    public void clearAllCaches() {
    }
}
