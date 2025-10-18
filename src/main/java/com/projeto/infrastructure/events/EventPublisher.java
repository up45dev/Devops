package com.projeto.infrastructure.events;

/**
 * Interface para publicação de eventos de domínio
 * Segue o Dependency Inversion Principle (DIP)
 * Permite diferentes implementações (Kafka, RabbitMQ, etc.)
 */
public interface EventPublisher {
    
    /**
     * Publica um evento de domínio
     * @param event o evento a ser publicado
     */
    void publish(Object event);
    
    /**
     * Publica um evento de domínio para um tópico específico
     * @param topic o tópico de destino
     * @param event o evento a ser publicado
     */
    void publish(String topic, Object event);
}
