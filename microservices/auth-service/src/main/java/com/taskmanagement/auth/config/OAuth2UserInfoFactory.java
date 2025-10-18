package com.taskmanagement.auth.config;

import com.taskmanagement.auth.entity.AuthProvider;

import java.util.Map;

/**
 * Factory para criar instâncias de OAuth2UserInfo baseado no provedor
 */
public class OAuth2UserInfoFactory {
    
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new IllegalArgumentException("Provedor OAuth2 não suportado: " + registrationId);
        }
    }
    
    /**
     * Implementação para Google OAuth2
     */
    public static class GoogleOAuth2UserInfo extends OAuth2UserInfo {
        
        public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
            super(attributes);
        }
        
        @Override
        public String getId() {
            return (String) attributes.get("sub");
        }
        
        @Override
        public String getName() {
            return (String) attributes.get("name");
        }
        
        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }
        
        @Override
        public String getImageUrl() {
            return (String) attributes.get("picture");
        }
    }
    
    /**
     * Implementação para GitHub OAuth2
     */
    public static class GithubOAuth2UserInfo extends OAuth2UserInfo {
        
        public GithubOAuth2UserInfo(Map<String, Object> attributes) {
            super(attributes);
        }
        
        @Override
        public String getId() {
            return String.valueOf(attributes.get("id"));
        }
        
        @Override
        public String getName() {
            return (String) attributes.get("name");
        }
        
        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }
        
        @Override
        public String getImageUrl() {
            return (String) attributes.get("avatar_url");
        }
    }
}