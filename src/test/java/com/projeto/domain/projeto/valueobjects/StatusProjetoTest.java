package com.projeto.domain.projeto.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para StatusProjeto
 * Seguem os princípios FIRST:
 * - Fast: Testes rápidos sem dependências externas
 * - Independent: Cada teste é independente
 * - Repeatable: Podem ser executados em qualquer ambiente
 * - Self-Validating: Resultado claro (pass/fail)
 * - Timely: Escritos junto com o código de produção
 */
@DisplayName("StatusProjeto Value Object")
class StatusProjetoTest {
    
    @Nested
    @DisplayName("Criação de Status")
    class CriacaoStatus {
        
        @Test
        @DisplayName("Deve criar status válido")
        void devecriarStatusValido() {
            // Given / When
            var status = StatusProjeto.of("PLANEJAMENTO");
            
            // Then
            assertThat(status.valor()).isEqualTo("PLANEJAMENTO");
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"PLANEJAMENTO", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO", "PAUSADO"})
        @DisplayName("Deve aceitar todos os status válidos")
        void deveAceitarStatusValidos(String statusValido) {
            // When / Then
            assertThatNoException().isThrownBy(() -> StatusProjeto.of(statusValido));
        }
        
        @Test
        @DisplayName("Deve converter para maiúsculo")
        void deveConverterParaMaiusculo() {
            // Given / When
            var status = StatusProjeto.of("planejamento");
            
            // Then
            assertThat(status.valor()).isEqualTo("PLANEJAMENTO");
        }
        
        @Test
        @DisplayName("Deve lançar exceção para status inválido")
        void deveLancarExcecaoParaStatusInvalido() {
            // When / Then
            assertThatThrownBy(() -> StatusProjeto.of("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Status inválido");
        }
        
        @Test
        @DisplayName("Deve lançar exceção para status nulo")
        void deveLancarExcecaoParaStatusNulo() {
            // When / Then
            assertThatThrownBy(() -> StatusProjeto.of(null))
                .isInstanceOf(NullPointerException.class);
        }
        
        @Test
        @DisplayName("Deve lançar exceção para status vazio")
        void deveLancarExcecaoParaStatusVazio() {
            // When / Then
            assertThatThrownBy(() -> StatusProjeto.of(""))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
    
    @Nested
    @DisplayName("Regras de Negócio")
    class RegrasNegocio {
        
        @Test
        @DisplayName("PLANEJAMENTO pode ser alterado para EM_ANDAMENTO")
        void planejamentoPodeSerAlteradoParaEmAndamento() {
            // Given
            var statusAtual = StatusProjeto.PLANEJAMENTO;
            var novoStatus = StatusProjeto.EM_ANDAMENTO;
            
            // When / Then
            assertThat(statusAtual.podeSerAlteradoPara(novoStatus)).isTrue();
        }
        
        @Test
        @DisplayName("PLANEJAMENTO não pode ser alterado para CONCLUIDO")
        void planejamentoNaoPodeSerAlteradoParaConcluido() {
            // Given
            var statusAtual = StatusProjeto.PLANEJAMENTO;
            var novoStatus = StatusProjeto.CONCLUIDO;
            
            // When / Then
            assertThat(statusAtual.podeSerAlteradoPara(novoStatus)).isFalse();
        }
        
        @Test
        @DisplayName("CONCLUIDO não pode ser alterado para nenhum status")
        void concluidoNaoPodeSerAlterado() {
            // Given
            var statusAtual = StatusProjeto.CONCLUIDO;
            
            // When / Then
            assertThat(statusAtual.podeSerAlteradoPara(StatusProjeto.EM_ANDAMENTO)).isFalse();
            assertThat(statusAtual.podeSerAlteradoPara(StatusProjeto.PLANEJAMENTO)).isFalse();
            assertThat(statusAtual.podeSerAlteradoPara(StatusProjeto.CANCELADO)).isFalse();
        }
        
        @Test
        @DisplayName("Deve identificar status ativo corretamente")
        void deveIdentificarStatusAtivo() {
            // When / Then
            assertThat(StatusProjeto.PLANEJAMENTO.estaAtivo()).isTrue();
            assertThat(StatusProjeto.EM_ANDAMENTO.estaAtivo()).isTrue();
            assertThat(StatusProjeto.CONCLUIDO.estaAtivo()).isFalse();
            assertThat(StatusProjeto.CANCELADO.estaAtivo()).isFalse();
        }
        
        @Test
        @DisplayName("Deve identificar status finalizado corretamente")
        void deveIdentificarStatusFinalizado() {
            // When / Then
            assertThat(StatusProjeto.CONCLUIDO.estaFinalizado()).isTrue();
            assertThat(StatusProjeto.CANCELADO.estaFinalizado()).isTrue();
            assertThat(StatusProjeto.PLANEJAMENTO.estaFinalizado()).isFalse();
            assertThat(StatusProjeto.EM_ANDAMENTO.estaFinalizado()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Igualdade e Hash")
    class IgualdadeHash {
        
        @Test
        @DisplayName("Deve ser igual quando valores são iguais")
        void deveSerIgualQuandoValoresSaoIguais() {
            // Given
            var status1 = StatusProjeto.of("PLANEJAMENTO");
            var status2 = StatusProjeto.of("PLANEJAMENTO");
            
            // When / Then
            assertThat(status1).isEqualTo(status2);
            assertThat(status1.hashCode()).isEqualTo(status2.hashCode());
        }
        
        @Test
        @DisplayName("Deve ser diferente quando valores são diferentes")
        void deveSerDiferenteQuandoValoresSaoDiferentes() {
            // Given
            var status1 = StatusProjeto.PLANEJAMENTO;
            var status2 = StatusProjeto.EM_ANDAMENTO;
            
            // When / Then
            assertThat(status1).isNotEqualTo(status2);
        }
    }
}
