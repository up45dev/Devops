package com.taskmanagement.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Auth Service - OAuth2 + JWT Authentication Service
 * Serviço de autenticação com OAuth2 e JWT para microserviços
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}