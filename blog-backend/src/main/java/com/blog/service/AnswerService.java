package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.dto.AnswerForm;
import com.blog.entity.Answer;

import java.util.List;

public interface AnswerService extends IService<Answer> {
    
    List<Answer> getAnswersByQuestionId(Long questionId);
    
    Answer createAnswer(Long userId, Long questionId, AnswerForm form);
    
    Answer updateAnswer(Long userId, Long id, AnswerForm form);
    
    void deleteAnswer(Long userId, Long id);
    
    void likeAnswer(Long id);
    
    void acceptAnswer(Long questionOwnerId, Long answerId);
}
