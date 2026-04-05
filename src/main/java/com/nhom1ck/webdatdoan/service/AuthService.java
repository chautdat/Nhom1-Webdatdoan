package com.nhom1ck.webdatdoan.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhom1ck.webdatdoan.dto.auth.AuthResponse;
import com.nhom1ck.webdatdoan.dto.auth.LoginRequest;
import com.nhom1ck.webdatdoan.dto.auth.RegisterRequest;
import com.nhom1ck.webdatdoan.entity.User;
import com.nhom1ck.webdatdoan.entity.UserRole;
import com.nhom1ck.webdatdoan.entity.UserStatus;
import com.nhom1ck.webdatdoan.exception.BadRequestException;
import com.nhom1ck.webdatdoan.exception.ResourceNotFoundException;
import com.nhom1ck.webdatdoan.repository.UserRepository;
import com.nhom1ck.webdatdoan.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // ✅ ĐĂNG KÝ - ĐÃ FIX
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.error("Email already exists");
        }

        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            return AuthResponse.error("Username already exists");
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.ROLE_CUSTOMER); // ✅ FIX: Dùng ROLE_CUSTOMER
        user.setStatus(UserStatus.active);

        userRepository.save(user);

        // Generate token
        String token = jwtService.generateToken(user);

        // User info
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setFullName(user.getFullName());
        userInfo.setRole(user.getRole().name()); // ✅ Trả về "ROLE_CUSTOMER"

        return AuthResponse.success("Registration successful", token, userInfo);
    }

    // ✅ ĐĂNG NHẬP - ĐÃ FIX
    public AuthResponse login(LoginRequest request) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("🔐 LOGIN ATTEMPT");
        System.out.println("📧 Username/Email: " + request.getUsername());
        
        // Authenticate
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), 
                request.getPassword()
            )
        );
        System.out.println("✅ Authentication successful");

        // ✅ FIX: Tìm user bằng username HOẶC email
        User user = userRepository.findByUsernameOrEmail(
                request.getUsername(), 
                request.getUsername()
            )
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        System.out.println("👤 User found: " + user.getUsername());
        System.out.println("🔐 Role: " + user.getRole());
        System.out.println("📊 Status: " + user.getStatus());

        // Check status
        if (user.getStatus() != UserStatus.active) {
            throw new BadRequestException("Account locked or no access permission");
        }

        // Generate token
        String token = jwtService.generateToken(user);
        System.out.println("🔑 Token generated");

        // User info
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setFullName(user.getFullName());
        userInfo.setRole(user.getRole().name()); // ✅ "ROLE_CUSTOMER"
        
        System.out.println("✅ Login successful for: " + user.getUsername());
        System.out.println("═══════════════════════════════════════════════════════");

        return AuthResponse.success("Login successful", token, userInfo);
    }

    // ✅ GET CURRENT USER
    public AuthResponse.UserInfo getCurrentUser(String identifier) {
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setFullName(user.getFullName());
        userInfo.setRole(user.getRole().name());

        return userInfo;
    }

    // ✅ VERIFY PASSWORD
    @Transactional
    public boolean verifyOldPassword(String identifier, String oldPassword) {
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    // ✅ CHANGE PASSWORD
    @Transactional
    public void changePassword(String identifier, String oldPassword, String newPassword) {
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        // Validate new password
        if (!isValidPassword(newPassword)) {
            throw new BadRequestException("Password does not meet security standards");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ✅ VALIDATE PASSWORD
    private boolean isValidPassword(String password) {
        return password.length() >= 8 && 
               password.matches(".*[A-Za-z].*") && 
               password.matches(".*\\d.*");
    }
}
