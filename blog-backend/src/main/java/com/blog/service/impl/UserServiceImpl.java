package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(String username, String password, String nickname, String email) {
        if (checkUsernameExists(username)) {
            throw new BusinessException("用户名已存在");
        }
        if (checkEmailExists(email)) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setEmail(email);
        user.setStatus(1);
        user.setRole("USER");

        save(user);
        log.info("用户注册成功: {}", username);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        log.info("用户登录成功: {}", username);
        return user;
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUserInfo(User user) {
        User currentUser = getById(user.getId());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }
        if (!currentUser.getUsername().equals(user.getUsername())) {
            if (checkUsernameExists(user.getUsername())) {
                throw new BusinessException("用户名已存在");
            }
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        updateById(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return count(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return count(new LambdaQueryWrapper<User>().eq(User::getEmail, email)) > 0;
    }
}
