package com.taskmanagement.auth.security;

import com.taskmanagement.auth.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Handler para sucesso na autenticação OAuth2
 * Gera JWT e redireciona o usuário
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Autowired
    private JwtService jwtService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        String targetUrl = determineTargetUrl(request, response, authentication);
        
        if (response.isCommitted()) {
            logger.debug("Response foi commitada. Não é possível redirecionar para " + targetUrl);
            return;
        }
        
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
    protected String determineTargetUrl(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) {
        
        String token = jwtService.generateToken(authentication);
        
        return UriComponentsBuilder.fromUriString("http://localhost:8091/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();
    }
}