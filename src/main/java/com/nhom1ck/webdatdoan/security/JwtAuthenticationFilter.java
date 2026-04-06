package com.pdq.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // ✅ Lấy Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        // ✅ DEBUG: Log request info
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("🔍 JWT FILTER - Incoming Request");
        System.out.println("📍 URI: " + request.getRequestURI());
        System.out.println("📍 Method: " + request.getMethod());
        System.out.println("📍 Origin: " + request.getHeader("Origin"));
        
        // ✅ Kiểm tra Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️  No Bearer token found in Authorization header");
            if (authHeader != null) {
                System.out.println("⚠️  Header value: " + authHeader.substring(0, Math.min(30, authHeader.length())));
            }
            System.out.println("➡️  Continuing without authentication...");
            System.out.println("═══════════════════════════════════════════════════════");
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // ✅ Extract JWT token
            jwt = authHeader.substring(7);
            System.out.println("🔑 JWT Token found");
            System.out.println("🔑 Token preview: " + jwt.substring(0, Math.min(30, jwt.length())) + "...");
            
            // ✅ Extract username/email từ token
            userEmail = jwtService.extractUsername(jwt);
            System.out.println("👤 Username extracted: " + userEmail);
            
            // ✅ Kiểm tra authentication
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("🔍 Loading user details for: " + userEmail);
                
                // ✅ Load user details từ DB để validate
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                System.out.println("👥 User loaded from DB: " + userDetails.getUsername());
                System.out.println("🔐 DB Authorities: " + userDetails.getAuthorities());
                
                // ✅ Validate token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    System.out.println("✅ Token is VALID");
                    
                    // ✅ FIX: Extract roles TỪ TOKEN, không dùng roles từ DB
                    Claims claims = jwtService.extractAllClaims(jwt);
                    
                    @SuppressWarnings("unchecked")
                    List<String> rolesFromToken = claims.get("roles", List.class);
                    
                    System.out.println("🔑 Roles from TOKEN: " + rolesFromToken);
                    
                    // ✅ Convert roles thành GrantedAuthority
                    List<GrantedAuthority> authorities;
                    
                    if (rolesFromToken != null && !rolesFromToken.isEmpty()) {
                        authorities = rolesFromToken.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                        
                        System.out.println("✅ Using roles from TOKEN: " + authorities);
                    } else {
                        // Fallback: Dùng roles từ DB nếu token không có
                        authorities = userDetails.getAuthorities().stream()
                                .collect(Collectors.toList());
                        
                        System.out.println("⚠️  Token has no roles, using DB roles: " + authorities);
                    }
                    
                    // ✅ Tạo authentication token với roles TỪ TOKEN
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities  // ← Dùng roles từ TOKEN
                    );
                    
                    // ✅ Set details
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // ✅ Set authentication vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    System.out.println("✅ Authentication set in SecurityContext");
                    System.out.println("✅ User authenticated: " + userEmail);
                    System.out.println("✅ Final Roles: " + authorities);
                    System.out.println("✅ Has ROLE_CUSTOMER: " + authorities.stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
                    System.out.println("✅ Has ROLE_ADMIN: " + authorities.stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
                    
                } else {
                    System.out.println("❌ Token validation FAILED");
                    System.out.println("❌ Token might be expired or invalid");
                }
            } else {
                if (userEmail == null) {
                    System.out.println("⚠️  Could not extract username from token");
                }
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("ℹ️  User already authenticated in SecurityContext");
                    System.out.println("ℹ️  Current auth: " + SecurityContextHolder.getContext().getAuthentication());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ JWT FILTER EXCEPTION:");
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("❌ Class: " + e.getClass().getName());
            e.printStackTrace();
        }
        
        System.out.println("➡️  Proceeding to next filter...");
        System.out.println("═══════════════════════════════════════════════════════");
        
        // ✅ Continue filter chain
        filterChain.doFilter(request, response);
    }
}