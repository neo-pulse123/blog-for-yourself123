package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.dto.NoteForm;
import com.blog.entity.Note;

public interface NoteService extends IService<Note> {
    
    IPage<Note> getNotePage(Page<Note> page, Long categoryId, String keyword, Long userId);
    
    Note getNoteById(Long id);
    
    Note createNote(Long userId, NoteForm form);
    
    Note updateNote(Long userId, Long id, NoteForm form);
    
    void deleteNote(Long userId, Long id);
    
    void incrementViewCount(Long id);
    
    void incrementLikeCount(Long id);
    
    IPage<Note> getMyNotes(Page<Note> page, Long userId);
}
