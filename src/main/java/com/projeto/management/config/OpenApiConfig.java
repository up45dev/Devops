package com.projeto.management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger/OpenAPI
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Sistema de Gerenciamento de Projetos e Tarefas")
                .description("API RESTful para gerenciamento de projetos e tarefas desenvolvida com Spring Boot")
                .version("1.0.0")
                .contact(new Contact()
                    .name("MiniMax Agent")
                    .email("contato@exemplo.com")
                    .url("https://exemplo.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor de Desenvolvimento"),
                new Server()
                    .url("https://api.exemplo.com")
                    .description("Servidor de Produção")
            ));
    }
}
