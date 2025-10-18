package com.taskmanagement.projeto.event;

import com.taskmanagement.projeto.entity.Projeto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Publisher de eventos para Kafka
 */
@Component
public class ProjetoEventPublisher {
    
    private static final String TOPIC = "projeto-events";
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void publishProjetoCriado(Projeto projeto) {
        publishEvent("PROJETO_CRIADO", projeto);
    }
    
    public void publishProjetoAtualizado(Projeto projeto) {
        publishEvent("PROJETO_ATUALIZADO", projeto);
    }
    
    public void publishProjetoIniciado(Projeto projeto) {
        publishEvent("PROJETO_INICIADO", projeto);
    }
    
    public void publishProjetoFinalizado(Projeto projeto) {
        publishEvent("PROJETO_FINALIZADO", projeto);
    }
    
    public void publishProjetoCancelado(Projeto projeto) {
        publishEvent("PROJETO_CANCELADO", projeto);
    }
    
    public void publishProjetoExcluido(Projeto projeto) {
        publishEvent("PROJETO_EXCLUIDO", projeto);
    }
    
    private void publishEvent(String eventType, Projeto projeto) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("timestamp", LocalDateTime.now());
            event.put("projetoId", projeto.getId());
            event.put("nome", projeto.getNome());
            event.put("status", projeto.getStatus());
            event.put("responsavel", projeto.getResponsavel());
            
            String eventJson = objectMapper.writeValueAsString(event);
            String key = "projeto-" + projeto.getId();
            
            kafkaTemplate.send(TOPIC, key, eventJson);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Erro ao publicar evento: " + e.getMessage());
        }
    }
}