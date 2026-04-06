package com.nhom1ck.webdatdoan.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Boolean success;
    private String message;
    private String token;
    private String refreshToken;
    private UserInfo user;

    public AuthResponse(Boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public static AuthResponse success(String message, String token, UserInfo user) {
        return new AuthResponse(true, message, token, null, user);
    }

    public static AuthResponse error(String message) {
        return new AuthResponse(false, message, null);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private String role;
    }
}
