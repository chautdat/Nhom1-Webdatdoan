package com.pdq.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.pdq.dto.common.ApiResponse;
import com.pdq.dto.user.ChangePasswordRequest;
import com.pdq.dto.user.UpdateProfileRequest;
import com.pdq.entity.User;
import com.pdq.entity.UserRole;
import com.pdq.entity.UserStatus;
import com.pdq.exception.BadRequestException;
import com.pdq.exception.ResourceNotFoundException;
import com.pdq.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================== GET ALL USERS ==================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Pageable pageable) {

        Page<User> users;

        if (search != null && !search.isEmpty()) {
            users = userRepository.findByFullNameContainingOrEmailContainingOrUsernameContainingOrPhoneContaining(
                    search, search, search, search, pageable);
        } else if (role != null && status != null) {
            UserRole roleEnum = parseRole(role);
            UserStatus statusEnum = parseStatus(status);
            users = userRepository.findByRoleAndStatus(roleEnum, statusEnum, pageable);
        } else if (role != null) {
            UserRole roleEnum = parseRole(role);
            users = userRepository.findByRole(roleEnum, pageable);
        } else if (status != null) {
            UserStatus statusEnum = parseStatus(status);
            users = userRepository.findByStatus(statusEnum, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    // ================== GET USER BY ID ==================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    // ================== GET USER COUNT ==================
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getUserCount() {
        Long count = userRepository.count();
        return ResponseEntity.ok(ApiResponse.success("User count retrieved", count));
    }

    // ================== SELF PROFILE (no id) ==================
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> getProfile(
            @AuthenticationPrincipal UserDetails principal) {
        User me = findUserByPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", me));
    }

    // ================== SELF/ADMIN PROFILE BY ID (fallback for FE) ==================
    @GetMapping("/profile/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> getProfileById(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        User target = resolveTargetUser(principal, id);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", target));
    }

    @PatchMapping("/profile/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> updateProfileByIdPatch(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return doUpdateProfile(principal, id, request);
    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> updateProfileByIdPut(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return doUpdateProfile(principal, id, request);
    }

    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> updateProfileNoIdPatch(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return doUpdateProfile(principal, null, request);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> updateProfileNoIdPut(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return doUpdateProfile(principal, null, request);
    }

    // ================== DELETE USER (ADMIN) ==================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == UserRole.ROLE_ADMIN) {
            throw new BadRequestException("Cannot delete admin user");
        }

        userRepository.delete(user);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

    private ResponseEntity<ApiResponse<User>> doUpdateProfile(UserDetails principal, Long id, UpdateProfileRequest request) {
        User target = resolveTargetUser(principal, id);

        target.setFullName(request.getFullName());
        target.setPhone(request.getPhone());
        target.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(target);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", target));
    }

    private User resolveTargetUser(UserDetails principal, Long id) {
        User requester = findUserByPrincipal(principal);
        if (requester.getRole() == UserRole.ROLE_ADMIN) {
            if (id == null) {
                return requester;
            }
            return userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        // Non-admin: always operate on self, ignore mismatched id to avoid 403 do user FE sai id
        if (id != null && !requester.getUserId().equals(id)) {
            // still allow self but log; no BadRequest to keep UX
        }
        return requester;
    }

    // ================== UPDATE USER STATUS ==================
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            throw new RuntimeException("Status is required");
        }

        UserStatus statusEnum = parseStatus(status);
        user.setStatus(statusEnum);
        User updated = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", updated));
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid role. Must be one of: ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER");
        }
    }

    private UserStatus parseStatus(String status) {
        try {
            return UserStatus.valueOf(status.toLowerCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid status. Must be: active, inactive, or banned");
        }
    }

    // ================== SELF PROFILE ==================
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> getMyProfile(@AuthenticationPrincipal UserDetails principal) {
        User user = findUserByPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", user));
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> updateMyProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request) {

        User user = findUserByPrincipal(principal);
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(request.getAvatarUrl());

        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
    }

    @PatchMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        User user = findUserByPrincipal(principal);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Confirm password does not match");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    private User findUserByPrincipal(UserDetails principal) {
        if (principal == null) {
            throw new BadRequestException("Unauthenticated");
        }
        String usernameOrEmail = principal.getUsername();
        return userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }
}
