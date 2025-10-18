package com.projeto.application.projeto;

import com.projeto.application.projeto.commands.CriarProjetoCommand;
import com.projeto.application.projeto.commands.AlterarStatusProjetoCommand;
import com.projeto.domain.projeto.Projeto;
import com.projeto.domain.projeto.ProjetoRepository;
import com.projeto.domain.projeto.valueobjects.StatusProjeto;
import com.projeto.infrastructure.events.EventPublisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Testes unitários para ProjetoApplicationService
 * Utiliza Mocks para isolar unidade sob teste
 * Implementa princípios FIRST
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjetoApplicationService")
class ProjetoApplicationServiceTest {
    
    @Mock
    private ProjetoRepository projetoRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private ProjetoApplicationService service;
    
    private static final String NOME_PROJETO = "Projeto Teste";
    private static final String DESCRICAO = "Descrição do projeto";
    private static final String RESPONSAVEL = "João Silva";
    private static final String USUARIO = "admin";
    private static final LocalDate DATA_INICIO = LocalDate.now();
    private static final LocalDate DATA_FIM = LocalDate.now().plusDays(30);
    
    @Nested
    @DisplayName("Criar Projeto")
    class CriarProjeto {
        
        @Test
        @DisplayName("Deve criar projeto com sucesso")
        void deveCriarProjetoComSucesso() {
            // Given
            var command = new CriarProjetoCommand(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, DATA_FIM, RESPONSAVEL, USUARIO
            );
            
            var projetoSalvo = mock(Projeto.class);
            when(projetoSalvo.getId()).thenReturn(1L);
            when(projetoRepository.existsByNome(NOME_PROJETO)).thenReturn(false);
            when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoSalvo);
            
            // When
            var projetoId = service.criarProjeto(command);
            
            // Then
            assertThat(projetoId).isEqualTo(1L);
            
            // Verifica interações
            verify(projetoRepository).existsByNome(NOME_PROJETO);
            verify(projetoRepository).save(any(Projeto.class));
            
            // Verifica que projeto foi criado com dados corretos
            ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
            verify(projetoRepository).save(projetoCaptor.capture());
            
            var projetoCriado = projetoCaptor.getValue();
            assertThat(projetoCriado.getNome()).isEqualTo(NOME_PROJETO);
            assertThat(projetoCriado.getResponsavel()).isEqualTo(RESPONSAVEL);
            assertThat(projetoCriado.getStatus()).isEqualTo(StatusProjeto.PLANEJAMENTO);
        }
        
        @Test
        @DisplayName("Deve lançar exceção quando projeto com nome já existe")
        void deveLancarExcecaoQuandoProjetoComNomeJaExiste() {
            // Given
            var command = new CriarProjetoCommand(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, DATA_FIM, RESPONSAVEL, USUARIO
            );
            
            when(projetoRepository.existsByNome(NOME_PROJETO)).thenReturn(true);
            
            // When / Then
            assertThatThrownBy(() -> service.criarProjeto(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe um projeto com o nome");
            
            // Verifica que save não foi chamado
            verify(projetoRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Deve publicar eventos de domínio")
        void devePublicarEventosDominio() {
            // Given
            var command = new CriarProjetoCommand(
                NOME_PROJETO, DESCRICAO, DATA_INICIO, DATA_FIM, RESPONSAVEL, USUARIO
            );
            
            var projetoSalvo = mock(Projeto.class);
            when(projetoSalvo.getId()).thenReturn(1L);
            when(projetoRepository.existsByNome(NOME_PROJETO)).thenReturn(false);
            when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoSalvo);
            
            // When
            service.criarProjeto(command);
            
            // Then
            verify(eventPublisher, atLeastOnce()).publish(any());
        }
    }
    
    @Nested
    @DisplayName("Alterar Status")
    class AlterarStatus {
        
        @Test
        @DisplayName("Deve iniciar projeto")
        void deveIniciarProjeto() {
            // Given
            var projetoId = 1L;
            var command = new AlterarStatusProjetoCommand(
                projetoId, "EM_ANDAMENTO", null, USUARIO
            );
            
            var projeto = mock(Projeto.class);
            when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));
            
            // When
            service.alterarStatus(command);
            
            // Then
            verify(projeto).iniciar(USUARIO);
            verify(projetoRepository).save(projeto);
        }
        
        @Test
        @DisplayName("Deve finalizar projeto")
        void deveFinalizarProjeto() {
            // Given
            var projetoId = 1L;
            var command = new AlterarStatusProjetoCommand(
                projetoId, "CONCLUIDO", null, USUARIO
            );
            
            var projeto = mock(Projeto.class);
            when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));
            
            // When
            service.alterarStatus(command);
            
            // Then
            verify(projeto).finalizar(USUARIO);
            verify(projetoRepository).save(projeto);
        }
        
        @Test
        @DisplayName("Deve cancelar projeto com motivo")
        void deveCancelarProjetoComMotivo() {
            // Given
            var projetoId = 1L;
            var motivo = "Falta de recursos";
            var command = new AlterarStatusProjetoCommand(
                projetoId, "CANCELADO", motivo, USUARIO
            );
            
            var projeto = mock(Projeto.class);
            when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));
            
            // When
            service.alterarStatus(command);
            
            // Then
            verify(projeto).cancelar(motivo, USUARIO);
            verify(projetoRepository).save(projeto);
        }
        
        @Test
        @DisplayName("Deve lançar exceção quando projeto não encontrado")
        void deveLancarExcecaoQuandoProjetoNaoEncontrado() {
            // Given
            var projetoId = 999L;
            var command = new AlterarStatusProjetoCommand(
                projetoId, "EM_ANDAMENTO", null, USUARIO
            );
            
            when(projetoRepository.findById(projetoId)).thenReturn(Optional.empty());
            
            // When / Then
            assertThatThrownBy(() -> service.alterarStatus(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Projeto não encontrado");
        }
        
        @Test
        @DisplayName("Deve lançar exceção para status inválido")
        void deveLancarExcecaoParaStatusInvalido() {
            // Given
            var projetoId = 1L;
            var command = new AlterarStatusProjetoCommand(
                projetoId, "STATUS_INVALIDO", null, USUARIO
            );
            
            var projeto = mock(Projeto.class);
            when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));
            
            // When / Then
            assertThatThrownBy(() -> service.alterarStatus(command))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
