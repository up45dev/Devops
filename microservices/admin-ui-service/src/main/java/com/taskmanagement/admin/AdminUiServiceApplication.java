package com.taskmanagement.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Admin UI Service - Interface administrativa com Vaadin
 * Interface rica para administração do sistema
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AdminUiServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AdminUiServiceApplication.class, args);
    }
}