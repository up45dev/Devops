package com.taskmanagement.gateway.filter;

import com.taskmanagement.gateway.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * Filtro de autenticação para API Gateway
 * Valida tokens JWT em requisições
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    
    @Autowired
    private JwtService jwtService;
    
    public AuthenticationFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            if (!containsAuthorization(request)) {
                return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }
            
            String token = extractToken(request);
            
            if (!jwtService.validateToken(token)) {
                return onError(exchange, "JWT token is invalid", HttpStatus.UNAUTHORIZED);
            }
            
            // Add user info to headers for downstream services
            String username = jwtService.extractUsername(token);
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Name", username)
                    .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }
    
    private boolean containsAuthorization(ServerHttpRequest request) {
        return request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
    }
    
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private Mono<Void> onError(org.springframework.web.server.ServerWebExchange exchange, 
                               String error, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
    
    public static class Config {
        // Configuration properties if needed
    }
}