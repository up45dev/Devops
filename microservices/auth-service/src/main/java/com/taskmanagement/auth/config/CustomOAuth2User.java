package com.taskmanagement.auth.config;

import com.taskmanagement.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * Implementação personalizada de OAuth2User
 * Combina informações do usuário local com dados OAuth2
 */
public class CustomOAuth2User implements OAuth2User {
    
    private User user;
    private Map<String, Object> attributes;
    
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }
    
    @Override
    public String getName() {
        return user.getUsername();
    }
    
    public User getUser() {
        return user;
    }
}