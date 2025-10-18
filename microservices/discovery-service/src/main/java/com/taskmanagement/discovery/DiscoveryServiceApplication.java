package com.taskmanagement.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Discovery Service - Eureka Server
 * Serviço central de descoberta para arquitetura de microserviços
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}