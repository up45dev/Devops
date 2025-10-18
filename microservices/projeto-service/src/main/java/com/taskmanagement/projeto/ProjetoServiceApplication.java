package com.taskmanagement.projeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Projeto Service - Microserviço para Gestão de Projetos
 * Utiliza HATEOAS para APIs maduras
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProjetoServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProjetoServiceApplication.class, args);
    }
}