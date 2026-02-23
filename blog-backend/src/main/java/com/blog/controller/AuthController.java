package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.AuthResponse;
import com.blog.dto.LoginRequest;
import com.blog.dto.RegisterRequest;
import com.blog.entity.User;
import com.blog.security.JwtUtils;
import com.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Result.badRequest("两次密码输入不一致");
        }

        User user = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                request.getEmail()
        );

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        AuthResponse response = new AuthResponse(
                token,
                jwtUtils.getExpiration(),
                new AuthResponse.UserInfo(
                        user.getId(),
                        user.getUsername(),
                        user.getNickname(),
                        user.getEmail(),
                        user.getAvatar(),
                        user.getRole()
                )
        );
        
        return Result.success("注册成功", response);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        AuthResponse response = new AuthResponse(
                token,
                jwtUtils.getExpiration(),
                new AuthResponse.UserInfo(
                        user.getId(),
                        user.getUsername(),
                        user.getNickname(),
                        user.getEmail(),
                        user.getAvatar(),
                        user.getRole()
                )
        );
        
        return Result.success("登录成功", response);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public Result<AuthResponse.UserInfo> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.getUserInfo(userId);
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getAvatar(),
                user.getRole()
        );
        
        return Result.success(userInfo);
    }

    @GetMapping("/check/username")
    @Operation(summary = "检查用户名是否存在")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        return Result.success(userService.checkUsernameExists(username));
    }

    @GetMapping("/check/email")
    @Operation(summary = "检查邮箱是否存在")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        return Result.success(userService.checkEmailExists(email));
    }
}
