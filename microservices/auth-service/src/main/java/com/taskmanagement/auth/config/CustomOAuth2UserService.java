package com.taskmanagement.auth.config;

import com.taskmanagement.auth.entity.AuthProvider;
import com.taskmanagement.auth.entity.Role;
import com.taskmanagement.auth.entity.User;
import com.taskmanagement.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Serviço personalizado para usuários OAuth2
 * Processa informações de provedores externos
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("Erro no processamento do usuário OAuth2: " + ex.getMessage());
        }
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            userRequest.getClientRegistration().getRegistrationId(), 
            oAuth2User.getAttributes()
        );
        
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email não encontrado no provedor OAuth2");
        }
        
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()))) {
                throw new OAuth2AuthenticationException(
                    "Parece que você se inscreveu com uma conta " + user.getProvider() + 
                    ". Por favor, use sua conta " + user.getProvider() + " para fazer login."
                );
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }
        
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
    
    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        
        user.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setFirstName(oAuth2UserInfo.getFirstName());
        user.setLastName(oAuth2UserInfo.getLastName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setAvatarUrl(oAuth2UserInfo.getImageUrl());
        user.setUsername(generateUsername(oAuth2UserInfo.getEmail()));
        user.setPassword(passwordEncoder.encode("oauth2_user"));
        user.setRoles(Set.of(Role.USER));
        
        return userRepository.save(user);
    }
    
    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setAvatarUrl(oAuth2UserInfo.getImageUrl());
        
        return userRepository.save(existingUser);
    }
    
    private String generateUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;
        
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }
}