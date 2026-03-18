package com.factory.factory_erp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.factory.factory_erp.model.Employee;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpirationMs;
    
    /**
     * Generate access token from Employee
     */
    public String generateAccessToken(Employee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", employee.getId());
        claims.put("firstName", employee.getFirstName());
        claims.put("lastName", employee.getLastName());
        claims.put("names", employee.getNames());
        claims.put("email", employee.getEmail());
        claims.put("position", employee.getPosition());
        claims.put("employeeType", employee.getEmployeeType());
        claims.put("status", employee.getStatus());
        claims.put("canLogin", employee.getCanLogin());
        claims.put("type", "access");
        
        return createToken(claims, employee.getEmail(), jwtExpirationMs);
    }
    
    /**
     * Generate refresh token from Employee
     */
    public String generateRefreshToken(Employee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", employee.getId());
        claims.put("email", employee.getEmail());
        claims.put("type", "refresh");
        
        return createToken(claims, employee.getEmail(), refreshTokenExpirationMs);
    }
    
    /**
     * Generate JWT token (legacy method for backward compatibility)
     */
    public String generateToken(Employee employee) {
        return generateAccessToken(employee);
    }
    
    /**
     * Create JWT token with custom expiration
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Get employee email (subject) from JWT token
     */
    public String getEmployeeEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get claims from token
     */
    public Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
