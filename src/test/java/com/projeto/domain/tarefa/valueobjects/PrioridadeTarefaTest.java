package com.projeto.domain.tarefa.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para PrioridadeTarefa
 * Implementação seguindo TDD e princípios FIRST
 */
@DisplayName("PrioridadeTarefa Value Object")
class PrioridadeTarefaTest {
    
    @Nested
    @DisplayName("Criação de Prioridade")
    class CriacaoPrioridade {
        
        @Test
        @DisplayName("Deve criar prioridade válida")
        void deveCriarPrioridadeValida() {
            // Given / When
            var prioridade = PrioridadeTarefa.of("ALTA");
            
            // Then
            assertThat(prioridade.valor()).isEqualTo("ALTA");
            assertThat(prioridade.peso()).isEqualTo(3);
        }
        
        @ParameterizedTest
        @MethodSource("prioridadesValidas")
        @DisplayName("Deve aceitar todas as prioridades válidas")
        void deveAceitarPrioridadesValidas(String valor, int pesoEsperado) {
            // When
            var prioridade = PrioridadeTarefa.of(valor);
            
            // Then
            assertThat(prioridade.valor()).isEqualTo(valor.toUpperCase());
            assertThat(prioridade.peso()).isEqualTo(pesoEsperado);
        }
        
        private static Stream<Arguments> prioridadesValidas() {
            return Stream.of(
                Arguments.of("BAIXA", 1),
                Arguments.of("MEDIA", 2),
                Arguments.of("ALTA", 3),
                Arguments.of("CRITICA", 4),
                Arguments.of("baixa", 1), // teste case insensitive
                Arguments.of("MÉDIA", 2)
            );
        }
        
        @Test
        @DisplayName("Deve lançar exceção para prioridade inválida")
        void deveLancarExcecaoParaPrioridadeInvalida() {
            // When / Then
            assertThatThrownBy(() -> PrioridadeTarefa.of("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prioridade inválida");
        }
        
        @Test
        @DisplayName("Deve lançar exceção para prioridade nula")
        void deveLancarExcecaoParaPrioridadeNula() {
            // When / Then
            assertThatThrownBy(() -> PrioridadeTarefa.of(null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
    
    @Nested
    @DisplayName("Comparação de Prioridades")
    class ComparacaoPrioridades {
        
        @Test
        @DisplayName("CRITICA deve ser maior que ALTA")
        void criticaDeveSerMaiorQueAlta() {
            // Given
            var critica = PrioridadeTarefa.CRITICA;
            var alta = PrioridadeTarefa.ALTA;
            
            // When / Then
            assertThat(critica.ehMaiorQue(alta)).isTrue();
            assertThat(alta.ehMaiorQue(critica)).isFalse();
        }
        
        @Test
        @DisplayName("BAIXA não deve ser maior que MEDIA")
        void baixaNaoDeveSerMaiorQueMedia() {
            // Given
            var baixa = PrioridadeTarefa.BAIXA;
            var media = PrioridadeTarefa.MEDIA;
            
            // When / Then
            assertThat(baixa.ehMaiorQue(media)).isFalse();
        }
        
        @Test
        @DisplayName("Prioridade não deve ser maior que ela mesma")
        void prioridadeNaoDeveSerMaiorQueElaMesma() {
            // Given
            var alta = PrioridadeTarefa.ALTA;
            
            // When / Then
            assertThat(alta.ehMaiorQue(alta)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Identificação de Tipos")
    class IdentificacaoTipos {
        
        @Test
        @DisplayName("Apenas CRITICA deve ser identificada como crítica")
        void apenasCtriticaDeveSerIdentificadaComoCritica() {
            // When / Then
            assertThat(PrioridadeTarefa.CRITICA.ehCritica()).isTrue();
            assertThat(PrioridadeTarefa.ALTA.ehCritica()).isFalse();
            assertThat(PrioridadeTarefa.MEDIA.ehCritica()).isFalse();
            assertThat(PrioridadeTarefa.BAIXA.ehCritica()).isFalse();
        }
        
        @Test
        @DisplayName("ALTA e CRITICA devem ser identificadas como alta prioridade")
        void altaECriticaDevemSerIdentificadasComoAltaPrioridade() {
            // When / Then
            assertThat(PrioridadeTarefa.CRITICA.ehAlta()).isTrue();
            assertThat(PrioridadeTarefa.ALTA.ehAlta()).isTrue();
            assertThat(PrioridadeTarefa.MEDIA.ehAlta()).isFalse();
            assertThat(PrioridadeTarefa.BAIXA.ehAlta()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Constantes Estáticas")
    class ConstantesEstaticas {
        
        @Test
        @DisplayName("Constantes devem ter valores corretos")
        void constantesDevemTerValoresCorretos() {
            // When / Then
            assertThat(PrioridadeTarefa.BAIXA.valor()).isEqualTo("BAIXA");
            assertThat(PrioridadeTarefa.BAIXA.peso()).isEqualTo(1);
            
            assertThat(PrioridadeTarefa.MEDIA.valor()).isEqualTo("MEDIA");
            assertThat(PrioridadeTarefa.MEDIA.peso()).isEqualTo(2);
            
            assertThat(PrioridadeTarefa.ALTA.valor()).isEqualTo("ALTA");
            assertThat(PrioridadeTarefa.ALTA.peso()).isEqualTo(3);
            
            assertThat(PrioridadeTarefa.CRITICA.valor()).isEqualTo("CRITICA");
            assertThat(PrioridadeTarefa.CRITICA.peso()).isEqualTo(4);
        }
    }
}
