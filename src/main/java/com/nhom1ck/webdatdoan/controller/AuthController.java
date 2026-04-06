package com.pdq.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pdq.dto.auth.AuthResponse;
import com.pdq.dto.auth.LoginRequest;
import com.pdq.dto.auth.RegisterRequest;
import com.pdq.dto.common.ApiResponse;
import com.pdq.exception.BadRequestException;
import com.pdq.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Phương thức đăng ký người dùng
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        
        if (!authResponse.getSuccess()) {
            // Trả về thông báo nếu người dùng đã tồn tại
            if (authResponse.getMessage().equals("User already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error("User already exists")
                );
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(authResponse.getMessage()));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Registration successful", authResponse));
    }

    // Phương thức đăng nhập người dùng
    @PostMapping("/login")
public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
    try {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiResponse.error("Invalid username or password")
        );
    } catch (BadRequestException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ApiResponse.error(e.getMessage())  // Custom error for account locked or no access
        );
    }
}


    // Lấy thông tin người dùng hiện tại
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        AuthResponse.UserInfo user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    // Thay đổi mật khẩu người dùng
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        
        // Kiểm tra mật khẩu cũ
        boolean isOldPasswordCorrect = authService.verifyOldPassword(userDetails.getUsername(), oldPassword);
        if (!isOldPasswordCorrect) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Old password is incorrect"));
        }

        // Kiểm tra tính mạnh của mật khẩu mới
        if (!isValidPassword(newPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Password does not meet security standards"));
        }

        // Thay đổi mật khẩu
        authService.changePassword(userDetails.getUsername(), oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    // Kiểm tra mật khẩu mới có đủ mạnh không (tối thiểu 8 ký tự, có chữ cái và số)
    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*\\d.*");
    }
}
