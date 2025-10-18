package com.taskmanagement.auth.dto;

/**
 * DTO para resposta JWT
 */
public class JwtResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    
    // Constructors
    public JwtResponse() {}
    
    public JwtResponse(String accessToken, String refreshToken, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}