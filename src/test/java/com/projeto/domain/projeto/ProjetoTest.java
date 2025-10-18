package com.projeto.domain.projeto;

import com.projeto.domain.projeto.valueobjects.StatusProjeto;
import com.projeto.domain.projeto.events.ProjetoEvent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para entidade Projeto
 * Implementa TDD com foco em regras de negócio
 * Segue princípios FIRST
 */
@DisplayName("Projeto Entity")
class ProjetoTest {
    
    private static final String NOME_PROJETO = "Projeto Teste";
    private static final String DESCRICAO = "Descrição do projeto";
    private static final String RESPONSAVEL = "João Silva";
    private static final String USUARIO = "admin";
    private static final LocalDate DATA_INICIO = LocalDate.now();
    private static final LocalDate DATA_FIM = LocalDate.now().plusDays(30);
    
    @Nested
    @DisplayName("Criação de Projeto")
    class CriacaoProjeto {
        
        @Test
        @DisplayName("Deve criar projeto com dados válidos")
        void deveCriarProjetoComDadosValidos() {
            // When
            var projeto = Projeto.criar(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, DATA_FIM, RESPONSAVEL, USUARIO
            );
            
            // Then
            assertThat(projeto.getNome()).isEqualTo(NOME_PROJETO);
            assertThat(projeto.getDescricao()).isEqualTo(DESCRICAO);
            assertThat(projeto.getResponsavel()).isEqualTo(RESPONSAVEL);
            assertThat(projeto.getStatus()).isEqualTo(StatusProjeto.PLANEJAMENTO);
            assertThat(projeto.getDataInicio()).isEqualTo(DATA_INICIO);
            assertThat(projeto.getDataFim()).isEqualTo(DATA_FIM);
            assertThat(projeto.getAuditInfo().criadoPor()).isEqualTo(USUARIO);
        }
        
        @Test
        @DisplayName("Deve gerar evento de projeto criado")
        void deveGerarEventoDeProjetoCriado() {
            // When
            var projeto = Projeto.criar(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, DATA_FIM, RESPONSAVEL, USUARIO
            );
            
            // Then
            assertThat(projeto.getEventos()).hasSize(1);
            assertThat(projeto.getEventos().get(0))
                .isInstanceOf(ProjetoEvent.ProjetoCriado.class);
        }
        
        @ParameterizedTest
        @MethodSource("dadosInvalidosParaCriacao")
        @DisplayName("Deve lançar exceção para dados inválidos")
        void deveLancarExcecaoParaDadosInvalidos(String nome, String responsavel, 
                                               Class<? extends Exception> exceptionType) {
            // When / Then
            assertThatThrownBy(() -> Projeto.criar(
                nome, DESCRICAO, DATA_INICIO, DATA_FIM, responsavel, USUARIO
            )).isInstanceOf(exceptionType);
        }
        
        private static Stream<Arguments> dadosInvalidosParaCriacao() {
            return Stream.of(
                Arguments.of(null, RESPONSAVEL, NullPointerException.class),
                Arguments.of("", RESPONSAVEL, IllegalArgumentException.class),
                Arguments.of("   ", RESPONSAVEL, IllegalArgumentException.class),
                Arguments.of(NOME_PROJETO, null, NullPointerException.class),
                Arguments.of("A".repeat(201), RESPONSAVEL, IllegalArgumentException.class)
            );
        }
        
        @Test
        @DisplayName("Deve lançar exceção quando data início posterior à data fim")
        void deveLancarExcecaoQuandoDataInicioPosteriordataFim() {
            // Given
            var dataInicio = LocalDate.now().plusDays(30);
            var dataFim = LocalDate.now();
            
            // When / Then
            assertThatThrownBy(() -> Projeto.criar(
                NOME_PROJETO, DESCRICAO, dataInicio, dataFim, RESPONSAVEL, USUARIO
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Data de início não pode ser posterior");
        }
    }
    
    @Nested
    @DisplayName("Transições de Status")
    class TransicoesStatus {
        
        private Projeto projeto;
        
        @BeforeEach
        void setUp() {
            projeto = Projeto.criar(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, DATA_FIM, RESPONSAVEL, USUARIO
            );
            projeto.limparEventos(); // Limpa evento de criação para testes isolados
        }
        
        @Test
        @DisplayName("Deve iniciar projeto em PLANEJAMENTO")
        void deveIniciarProjetoEmPlanejamento() {
            // When
            projeto.iniciar(USUARIO);
            
            // Then
            assertThat(projeto.getStatus()).isEqualTo(StatusProjeto.EM_ANDAMENTO);
            assertThat(projeto.getEventos()).hasSize(1);
            assertThat(projeto.getEventos().get(0))
                .isInstanceOf(ProjetoEvent.ProjetoIniciado.class);
        }
        
        @Test
        @DisplayName("Deve finalizar projeto em EM_ANDAMENTO")
        void deveFinalizarProjetoEmAndamento() {
            // Given
            projeto.iniciar(USUARIO);
            projeto.limparEventos();
            
            // When
            projeto.finalizar(USUARIO);
            
            // Then
            assertThat(projeto.getStatus()).isEqualTo(StatusProjeto.CONCLUIDO);
            assertThat(projeto.getEventos()).hasSize(1);
            assertThat(projeto.getEventos().get(0))
                .isInstanceOf(ProjetoEvent.ProjetoFinalizado.class);
        }
        
        @Test
        @DisplayName("Deve cancelar projeto ativo")
        void deveCancelarProjetoAtivo() {
            // Given
            var motivo = "Cancelado por falta de recursos";
            
            // When
            projeto.cancelar(motivo, USUARIO);
            
            // Then
            assertThat(projeto.getStatus()).isEqualTo(StatusProjeto.CANCELADO);
            assertThat(projeto.getEventos()).hasSize(1);
            
            var evento = (ProjetoEvent.ProjetoCancelado) projeto.getEventos().get(0);
            assertThat(evento.motivo()).isEqualTo(motivo);
        }
        
        @Test
        @DisplayName("Não deve finalizar projeto em PLANEJAMENTO")
        void naoDeveFinalizarProjetoEmPlanejamento() {
            // When / Then
            assertThatThrownBy(() -> projeto.finalizar(USUARIO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Não é possível finalizar projeto");
        }
        
        @Test
        @DisplayName("Não deve alterar projeto já finalizado")
        void naoDeveAlterarProjetoJaFinalizado() {
            // Given
            projeto.iniciar(USUARIO);
            projeto.finalizar(USUARIO);
            
            // When / Then
            assertThatThrownBy(() -> projeto.iniciar(USUARIO))
                .isInstanceOf(IllegalStateException.class);
        }
    }
    
    @Nested
    @DisplayName("Atualização de Dados")
    class AtualizacaoDados {
        
        private Projeto projeto;
        
        @BeforeEach
        void setUp() {
            projeto = Projeto.criar(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, DATA_FIM, RESPONSAVEL, USUARIO
            );
            projeto.limparEventos();
        }
        
        @Test
        @DisplayName("Deve atualizar dados do projeto ativo")
        void deveAtualizarDadosProjetoAtivo() {
            // Given
            var novoNome = "Projeto Atualizado";
            var novaDescricao = "Nova descrição";
            var novaDataFim = DATA_FIM.plusDays(10);
            var novoResponsavel = "Maria Santos";
            
            // When
            projeto.atualizar(novoNome, novaDescricao, novaDataFim, novoResponsavel, USUARIO);
            
            // Then
            assertThat(projeto.getNome()).isEqualTo(novoNome);
            assertThat(projeto.getDescricao()).isEqualTo(novaDescricao);
            assertThat(projeto.getDataFim()).isEqualTo(novaDataFim);
            assertThat(projeto.getResponsavel()).isEqualTo(novoResponsavel);
            assertThat(projeto.getEventos()).hasSize(1);
            assertThat(projeto.getEventos().get(0))
                .isInstanceOf(ProjetoEvent.ProjetoAtualizado.class);
        }
        
        @Test
        @DisplayName("Não deve atualizar projeto finalizado")
        void naoDeveAtualizarProjetoFinalizado() {
            // Given
            projeto.iniciar(USUARIO);
            projeto.finalizar(USUARIO);
            
            // When / Then
            assertThatThrownBy(() -> projeto.atualizar(
                "Novo Nome", "Nova Desc", DATA_FIM, "Novo Resp", USUARIO
            )).isInstanceOf(IllegalStateException.class)
              .hasMessageContaining("Não é possível atualizar projeto finalizado");
        }
    }
    
    @Nested
    @DisplayName("Regras de Negócio")
    class RegrasNegocio {
        
        @Test
        @DisplayName("Projeto deve estar atrasado quando data fim é anterior a hoje")
        void projetoDeveEstarAtrasadoQuandoDataFimAnteriorHoje() {
            // Given
            var dataFimPassada = LocalDate.now().minusDays(1);
            var projeto = Projeto.criar(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, dataFimPassada, RESPONSAVEL, USUARIO
            );
            projeto.iniciar(USUARIO);
            
            // When / Then
            assertThat(projeto.estaAtrasado()).isTrue();
        }
        
        @Test
        @DisplayName("Projeto finalizado não deve estar atrasado")
        void projetoFinalizadoNaoDeveEstarAtrasado() {
            // Given
            var dataFimPassada = LocalDate.now().minusDays(1);
            var projeto = Projeto.criar(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, dataFimPassada, RESPONSAVEL, USUARIO
            );
            projeto.iniciar(USUARIO);
            projeto.finalizar(USUARIO);
            
            // When / Then
            assertThat(projeto.estaAtrasado()).isFalse();
        }
        
        @Test
        @DisplayName("Projeto sem data fim não deve estar atrasado")
        void projetoSemDataFimNaoDeveEstarAtrasado() {
            // Given
            var projeto = Projeto.criar(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, null, RESPONSAVEL, USUARIO
            );
            
            // When / Then
            assertThat(projeto.estaAtrasado()).isFalse();
        }
    }
}
