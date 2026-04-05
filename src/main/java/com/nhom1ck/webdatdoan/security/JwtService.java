package com.nhom1ck.webdatdoan.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // ✅ Extract username từ token
    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            System.out.println("🔍 Extracted username: " + username);
            return username;
        } catch (RuntimeException e) {
            System.err.println("❌ Error extracting username: " + e.getMessage());
            throw e;
        }
    }

    // ✅ Extract claim cụ thể
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ FIX: Extract all claims - PHẢI PUBLIC để JwtAuthenticationFilter dùng
    public Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("📋 Claims extracted:");
            System.out.println("   - Subject: " + claims.getSubject());
            System.out.println("   - Roles: " + claims.get("roles"));
            System.out.println("   - Issued: " + claims.getIssuedAt());
            System.out.println("   - Expires: " + claims.getExpiration());

            return claims;

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("❌ Token has EXPIRED");
            throw e;
        } catch (io.jsonwebtoken.SignatureException e) {
            System.err.println("❌ Invalid token SIGNATURE");
            throw e;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.err.println("❌ Malformed JWT token");
            throw e;
        } catch (RuntimeException e) {
            System.err.println("❌ Error parsing token: " + e.getMessage());
            throw e;
        }
    }

    // ✅ Generate token (simple)
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // ✅ FIX: Generate token với roles
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("🔨 GENERATING JWT TOKEN");
        System.out.println("👤 Username: " + userDetails.getUsername());
        System.out.println("🔐 Authorities: " + userDetails.getAuthorities());

        // ✅ FIX: Thêm roles vào claims dạng List<String>
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        extraClaims.put("roles", roles);

        System.out.println("🔑 Roles added to token: " + roles);

        String token = buildToken(extraClaims, userDetails, jwtExpiration);

        System.out.println("✅ Token generated successfully");
        System.out.println("🔑 Token preview: " + token.substring(0, Math.min(30, token.length())) + "...");
        System.out.println("⏰ Issued at: " + new Date(System.currentTimeMillis()));
        System.out.println("⏰ Expires at: " + new Date(System.currentTimeMillis() + jwtExpiration));
        System.out.println("⏰ Duration: " + (jwtExpiration / 1000 / 60) + " minutes");
        System.out.println("═══════════════════════════════════════════════════════");

        return token;
    }

    // ✅ Build token
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // nếu username = email thì giữ nguyên
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Validate token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            System.out.println("🔍 Validating token...");

            final String username = extractUsername(token);
            boolean usernameMatches = username.equals(userDetails.getUsername());
            boolean notExpired = !isTokenExpired(token);

            System.out.println("🔍 Token validation details:");
            System.out.println("   ✓ Token username: " + username);
            System.out.println("   ✓ Expected username: " + userDetails.getUsername());
            System.out.println("   ✓ Username matches: " + usernameMatches);
            System.out.println("   ✓ Not expired: " + notExpired);

            if (notExpired) {
                Date expiration = extractExpiration(token);
                long remainingTime = expiration.getTime() - System.currentTimeMillis();
                System.out.println("   ✓ Remaining time: " + (remainingTime / 1000 / 60) + " minutes");
            }

            boolean isValid = usernameMatches && notExpired;
            System.out.println("   " + (isValid ? "✅ Token is VALID" : "❌ Token is INVALID"));

            return isValid;

        } catch (RuntimeException e) {
            System.err.println("❌ Token validation exception: " + e.getMessage());
            return false;
        }
    }

    // ✅ Check if token expired
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            boolean expired = expiration.before(new Date());

            if (expired) {
                System.out.println("⚠️  Token EXPIRED at: " + expiration);
                System.out.println("⚠️  Current time: " + new Date());
            }

            return expired;
        } catch (RuntimeException e) {
            System.err.println("❌ Error checking token expiration: " + e.getMessage());
            return true; // Treat as expired if can't parse
        }
    }

    // ✅ Extract expiration date
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ✅ Get signing key
    private Key getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (RuntimeException e) {
            System.err.println("❌ Error decoding secret key: " + e.getMessage());
            System.err.println("❌ Make sure jwt.secret is a valid Base64 string");
            throw e;
        }
    }
}
