package com.projeto.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuração para serialização JSON
 * Configura ObjectMapper para trabalhar com Java 21 e time types
 */
@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Suporte para tipos de data/hora do Java 8+
        mapper.registerModule(new JavaTimeModule());
        
        // Desabilita escrita de datas como timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Configurações para Record classes do Java 21
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        return mapper;
    }
}
