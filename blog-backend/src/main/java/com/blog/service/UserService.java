package com.blog.service;

import com.blog.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.exception.BusinessException;

public interface UserService extends IService<User> {
    
    User register(String username, String password, String nickname, String email);
    
    User login(String username, String password);
    
    User getUserInfo(Long userId);
    
    void updateUserInfo(User user);
    
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    boolean checkUsernameExists(String username);
    
    boolean checkEmailExists(String email);
}
