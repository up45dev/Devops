package com.projeto.management.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do ModelMapper
 */
@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Configurações globais
        mapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        
        // Mapeamentos customizados podem ser adicionados aqui
        // configurarMapeamentosCustomizados(mapper);
        
        return mapper;
    }
    
    // Método para configurações de mapeamento específicas
    // private void configurarMapeamentosCustomizados(ModelMapper mapper) {
    //     // Exemplo de mapeamento customizado
    //     mapper.createTypeMap(ProjetoRequestDTO.class, Projeto.class)
    //         .addMappings(mapping -> {
    //             mapping.skip(Projeto::setId);
    //             mapping.skip(Projeto::setDataCriacao);
    //         });
    // }
}
