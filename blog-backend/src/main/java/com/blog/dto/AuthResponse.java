package com.blog.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;

    public AuthResponse(String token, Long expiresIn, UserInfo user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String avatar;
        private String role;

        public UserInfo(Long id, String username, String nickname, String email, String avatar, String role) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.email = email;
            this.avatar = avatar;
            this.role = role;
        }
    }
}
