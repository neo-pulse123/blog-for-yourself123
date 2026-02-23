package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.dto.QuestionForm;
import com.blog.entity.Question;

public interface QuestionService extends IService<Question> {
    
    IPage<Question> getQuestionPage(Page<Question> page, String keyword, Long userId);
    
    Question getQuestionById(Long id);
    
    Question createQuestion(Long userId, QuestionForm form);
    
    Question updateQuestion(Long userId, Long id, QuestionForm form);
    
    void deleteQuestion(Long userId, Long id);
    
    void incrementViewCount(Long id);
    
    IPage<Question> getMyQuestions(Page<Question> page, Long userId);
}
