package com.projeto.integration;

import com.projeto.infrastructure.events.kafka.KafkaEventPublisher;
import com.projeto.domain.projeto.events.ProjetoEvent;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Teste de integração para Kafka
 * Utiliza EmbeddedKafka para testes isolados
 * Segue princípios FIRST
 */
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = {"projeto-events", "tarefa-events"},
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Kafka Integration Tests")
class KafkaIntegrationTest {
    
    @Autowired
    private KafkaEventPublisher eventPublisher;
    
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Consumer<String, String> consumer;
    
    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer();
    }
    
    @Test
    @DisplayName("Deve publicar evento de projeto criado no tópico correto")
    void devePublicarEventoProjetoCriadoNoTopicoCorreto() throws Exception {
        // Given
        var evento = new ProjetoEvent.ProjetoCriado(1L, "Projeto Teste", LocalDateTime.now());
        consumer.subscribe(Collections.singletonList("projeto-events"));
        
        // When
        eventPublisher.publish(evento);
        
        // Then
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        
        assertThat(records).isNotEmpty();
        assertThat(records.count()).isEqualTo(1);
        
        var record = records.iterator().next();
        assertThat(record.topic()).isEqualTo("projeto-events");
        assertThat(record.key()).isEqualTo("projeto-1");
        
        // Verifica se o evento foi serializado corretamente
        var eventoDeserializado = objectMapper.readValue(record.value(), ProjetoEvent.ProjetoCriado.class);
        assertThat(eventoDeserializado.projetoId()).isEqualTo(1L);
        assertThat(eventoDeserializado.nome()).isEqualTo("Projeto Teste");
    }
    
    @Test
    @DisplayName("Deve publicar evento no tópico especificado")
    void devePublicarEventoNoTopicoEspecificado() throws Exception {
        // Given
        var evento = new ProjetoEvent.ProjetoFinalizado(2L, "Projeto Concluído", LocalDateTime.now());
        var topicoCustomizado = "custom-topic";
        
        embeddedKafkaBroker.addTopics(topicoCustomizado);
        consumer.subscribe(Collections.singletonList(topicoCustomizado));
        
        // When
        eventPublisher.publish(topicoCustomizado, evento);
        
        // Then
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        
        assertThat(records).isNotEmpty();
        var record = records.iterator().next();
        assertThat(record.topic()).isEqualTo(topicoCustomizado);
        assertThat(record.key()).isEqualTo("projeto-2");
    }
    
    @Test
    @DisplayName("Deve gerar chave de particionamento correta")
    void deveGerarChaveParticioamentooCorreta() {
        // Given
        var projetoId = 123L;
        var evento = new ProjetoEvent.ProjetoIniciado(projetoId, "Projeto Iniciado", LocalDateTime.now());
        consumer.subscribe(Collections.singletonList("projeto-events"));
        
        // When
        eventPublisher.publish(evento);
        
        // Then
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
        
        assertThat(records).isNotEmpty();
        var record = records.iterator().next();
        assertThat(record.key()).isEqualTo("projeto-123");
    }
    
    @Test
    @DisplayName("Deve lidar com erro de serialização graciosamente")
    void deveLidarComErroSerializacaoGraciosamente() {
        // Given
        var objetoInvalido = new Object() {
            // Objeto que pode causar erro na serialização
            private final Object self = this; // Referência circular
        };
        
        // When / Then
        assertThatThrownBy(() -> eventPublisher.publish(objetoInvalido))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Falha na serialização do evento");
    }
}