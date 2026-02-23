package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dto.QuestionForm;
import com.blog.entity.Question;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.mapper.QuestionMapper;
import com.blog.service.QuestionService;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final UserService userService;

    @Override
    public IPage<Question> getQuestionPage(Page<Question> page, String keyword, Long userId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getStatus, 1);
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Question::getTitle, keyword);
        }
        if (userId != null) {
            wrapper.eq(Question::getUserId, userId);
        }
        
        wrapper.orderByDesc(Question::getCreateTime);
        
        IPage<Question> result = page(page, wrapper);
        
        result.getRecords().forEach(question -> {
            User user = userService.getById(question.getUserId());
            if (user != null) {
                question.setUser(user);
            }
        });
        
        return result;
    }

    @Override
    public Question getQuestionById(Long id) {
        Question question = getById(id);
        if (question == null || question.getStatus() == 3) {
            throw new BusinessException("问题不存在");
        }
        
        User user = userService.getById(question.getUserId());
        if (user != null) {
            question.setUser(user);
        }
        
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Question createQuestion(Long userId, QuestionForm form) {
        Question question = new Question();
        question.setUserId(userId);
        question.setTitle(form.getTitle());
        question.setContent(form.getContent());
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setStatus(1);
        
        save(question);
        log.info("用户 {} 创建问题: {}", userId, question.getTitle());
        
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Question updateQuestion(Long userId, Long id, QuestionForm form) {
        Question question = getById(id);
        if (question == null) {
            throw new BusinessException("问题不存在");
        }
        if (!question.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此问题");
        }
        
        question.setTitle(form.getTitle());
        question.setContent(form.getContent());
        
        updateById(question);
        log.info("用户 {} 更新问题: {}", userId, question.getTitle());
        
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestion(Long userId, Long id) {
        Question question = getById(id);
        if (question == null) {
            throw new BusinessException("问题不存在");
        }
        if (!question.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此问题");
        }
        
        question.setStatus(3);
        updateById(question);
        log.info("用户 {} 删除问题: {}", userId, id);
    }

    @Override
    public void incrementViewCount(Long id) {
        Question question = getById(id);
        if (question != null) {
            question.setViewCount(question.getViewCount() + 1);
            updateById(question);
        }
    }

    @Override
    public IPage<Question> getMyQuestions(Page<Question> page, Long userId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getUserId, userId);
        wrapper.ne(Question::getStatus, 3);
        wrapper.orderByDesc(Question::getCreateTime);
        
        return page(page, wrapper);
    }
}
