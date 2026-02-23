package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dto.AnswerForm;
import com.blog.entity.Answer;
import com.blog.entity.Question;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.mapper.AnswerMapper;
import com.blog.service.AnswerService;
import com.blog.service.QuestionService;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements AnswerService {

    private final UserService userService;
    private final QuestionService questionService;

    @Override
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getQuestionId, questionId);
        wrapper.orderByDesc(Answer::getIsAccepted);
        wrapper.orderByDesc(Answer::getLikeCount);
        wrapper.orderByAsc(Answer::getCreateTime);
        
        List<Answer> answers = list(wrapper);
        
        answers.forEach(answer -> {
            User user = userService.getById(answer.getUserId());
            if (user != null) {
                answer.setUser(user);
            }
        });
        
        return answers;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Answer createAnswer(Long userId, Long questionId, AnswerForm form) {
        Question question = questionService.getById(questionId);
        if (question == null || question.getStatus() == 3) {
            throw new BusinessException("问题不存在");
        }
        
        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setUserId(userId);
        answer.setContent(form.getContent());
        answer.setLikeCount(0);
        answer.setIsAccepted(0);
        
        save(answer);
        
        question.setAnswerCount(question.getAnswerCount() + 1);
        questionService.updateById(question);
        
        log.info("用户 {} 回答问题: {}", userId, questionId);
        
        return answer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Answer updateAnswer(Long userId, Long id, AnswerForm form) {
        Answer answer = getById(id);
        if (answer == null) {
            throw new BusinessException("回答不存在");
        }
        if (!answer.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此回答");
        }
        
        answer.setContent(form.getContent());
        updateById(answer);
        
        log.info("用户 {} 更新回答: {}", userId, id);
        
        return answer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnswer(Long userId, Long id) {
        Answer answer = getById(id);
        if (answer == null) {
            throw new BusinessException("回答不存在");
        }
        if (!answer.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此回答");
        }
        
        Question question = questionService.getById(answer.getQuestionId());
        if (question != null) {
            question.setAnswerCount(Math.max(0, question.getAnswerCount() - 1));
            questionService.updateById(question);
        }
        
        removeById(id);
        log.info("用户 {} 删除回答: {}", userId, id);
    }

    @Override
    public void likeAnswer(Long id) {
        Answer answer = getById(id);
        if (answer != null) {
            answer.setLikeCount(answer.getLikeCount() + 1);
            updateById(answer);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptAnswer(Long questionOwnerId, Long answerId) {
        Answer answer = getById(answerId);
        if (answer == null) {
            throw new BusinessException("回答不存在");
        }
        
        Question question = questionService.getById(answer.getQuestionId());
        if (question == null) {
            throw new BusinessException("问题不存在");
        }
        if (!question.getUserId().equals(questionOwnerId)) {
            throw new BusinessException("无权限采纳此回答");
        }
        
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getQuestionId, answer.getQuestionId());
        List<Answer> answers = list(wrapper);
        
        answers.forEach(a -> {
            a.setIsAccepted(0);
            updateById(a);
        });
        
        answer.setIsAccepted(1);
        updateById(answer);
        
        log.info("用户 {} 采纳回答: {}", questionOwnerId, answerId);
    }
}
