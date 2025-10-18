package com.taskmanagement.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Serviço para geração e validação de tokens JWT
 */
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    
    @Value("${jwt.refresh-expiration}")
    private int jwtRefreshExpirationMs;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Gera token JWT para autenticação
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateToken(userPrincipal.getUsername());
    }
    
    /**
     * Gera token JWT para username
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, jwtExpirationMs);
    }
    
    /**
     * Gera refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, jwtRefreshExpirationMs);
    }
    
    /**
     * Gera token com claims customizados
     */
    public String generateToken(String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return createToken(claims, username, jwtExpirationMs);
    }
    
    private String createToken(Map<String, Object> claims, String subject, int expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Extrai username do token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extrai data de expiração do token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extrai claim específico do token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Verifica se o token expirou
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Valida o token
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Valida o token sem UserDetails
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }
    
    /**
     * Verifica se é um refresh token
     */
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("type"));
        } catch (JwtException e) {
            return false;
        }
    }
}