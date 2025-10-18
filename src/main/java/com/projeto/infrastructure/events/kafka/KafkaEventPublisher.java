package com.projeto.infrastructure.events.kafka;

import com.projeto.infrastructure.events.EventPublisher;
import com.projeto.domain.projeto.events.ProjetoEvent;
import com.projeto.domain.tarefa.events.TarefaEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementação do EventPublisher usando Apache Kafka
 * Segue o princípio de Substituição de Liskov (LSP)
 */
@Component
public class KafkaEventPublisher implements EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    // Tópicos Kafka
    private static final String PROJETO_EVENTS_TOPIC = "projeto-events";
    private static final String TAREFA_EVENTS_TOPIC = "tarefa-events";
    
    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                              ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void publish(Object event) {
        try {
            String topic = determinarTopico(event);
            String eventJson = objectMapper.writeValueAsString(event);
            String key = gerarChave(event);
            
            kafkaTemplate.send(topic, key, eventJson)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("Evento publicado com sucesso: {} para tópico: {}", 
                                  event.getClass().getSimpleName(), topic);
                    } else {
                        logger.error("Erro ao publicar evento: {} para tópico: {}", 
                                   event.getClass().getSimpleName(), topic, ex);
                    }
                });
                
        } catch (JsonProcessingException e) {
            logger.error("Erro ao serializar evento: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Falha na serialização do evento", e);
        }
    }
    
    @Override
    public void publish(String topic, Object event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            String key = gerarChave(event);
            
            kafkaTemplate.send(topic, key, eventJson)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("Evento publicado com sucesso: {} para tópico: {}", 
                                  event.getClass().getSimpleName(), topic);
                    } else {
                        logger.error("Erro ao publicar evento: {} para tópico: {}", 
                                   event.getClass().getSimpleName(), topic, ex);
                    }
                });
                
        } catch (JsonProcessingException e) {
            logger.error("Erro ao serializar evento: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Falha na serialização do evento", e);
        }
    }
    
    /**
     * Determina o tópico baseado no tipo do evento
     * Aplica o princípio Open/Closed (OCP)
     */
    private String determinarTopico(Object event) {
        return switch (event) {
            case ProjetoEvent projetoEvent -> PROJETO_EVENTS_TOPIC;
            case TarefaEvent tarefaEvent -> TAREFA_EVENTS_TOPIC;
            default -> throw new IllegalArgumentException(
                "Tipo de evento não suportado: " + event.getClass().getName()
            );
        };
    }
    
    /**
     * Gera chave para particionamento no Kafka
     * Garante que eventos da mesma entidade sejam processados em ordem
     */
    private String gerarChave(Object event) {
        return switch (event) {
            case ProjetoEvent projetoEvent -> "projeto-" + projetoEvent.projetoId();
            case TarefaEvent tarefaEvent -> "tarefa-" + tarefaEvent.tarefaId();
            default -> "unknown";
        };
    }
}
