package com.projeto.management.service;

import com.projeto.management.dto.request.ProjetoRequestDTO;
import com.projeto.management.dto.response.ProjetoResponseDTO;
import com.projeto.management.exception.EntityNotFoundException;
import com.projeto.management.exception.BusinessException;
import com.projeto.management.model.entity.Projeto;
import com.projeto.management.model.enums.StatusProjeto;
import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProjetoService
 */
@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {
    
    @Mock
    private ProjetoRepository projetoRepository;
    
    @Mock
    private ModelMapper modelMapper;
    
    @InjectMocks
    private ProjetoService projetoService;
    
    private ProjetoRequestDTO projetoRequestDTO;
    private Projeto projeto;
    private ProjetoResponseDTO projetoResponseDTO;
    
    @BeforeEach
    void setUp() {
        projetoRequestDTO = ProjetoRequestDTO.builder()
            .nome("Projeto Teste")
            .descricao("Descrição do projeto teste")
            .dataInicio(LocalDate.now())
            .dataFimPrevista(LocalDate.now().plusDays(30))
            .prioridade(Prioridade.ALTA)
            .responsavel("Responsável Teste")
            .orcamento(new BigDecimal("10000.00"))
            .build();
        
        projeto = Projeto.builder()
            .id(1L)
            .nome("Projeto Teste")
            .descricao("Descrição do projeto teste")
            .dataInicio(LocalDate.now())
            .dataFimPrevista(LocalDate.now().plusDays(30))
            .status(StatusProjeto.PLANEJAMENTO)
            .prioridade(Prioridade.ALTA)
            .responsavel("Responsável Teste")
            .orcamento(new BigDecimal("10000.00"))
            .ativo(true)
            .build();
        
        projetoResponseDTO = ProjetoResponseDTO.builder()
            .id(1L)
            .nome("Projeto Teste")
            .descricao("Descrição do projeto teste")
            .status(StatusProjeto.PLANEJAMENTO)
            .prioridade(Prioridade.ALTA)
            .responsavel("Responsável Teste")
            .ativo(true)
            .build();
    }
    
    @Test
    void criarProjeto_DeveRetornarProjetoResponseDTO_QuandoDadosValidos() {
        // Given
        when(modelMapper.map(projetoRequestDTO, Projeto.class)).thenReturn(projeto);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(modelMapper.map(projeto, ProjetoResponseDTO.class)).thenReturn(projetoResponseDTO);
        
        // When
        ProjetoResponseDTO resultado = projetoService.criarProjeto(projetoRequestDTO, "usuario-teste");
        
        // Then
        assertNotNull(resultado);
        assertEquals("Projeto Teste", resultado.getNome());
        assertEquals(StatusProjeto.PLANEJAMENTO, resultado.getStatus());
        
        verify(projetoRepository).save(any(Projeto.class));
        verify(modelMapper).map(projetoRequestDTO, Projeto.class);
        verify(modelMapper).map(projeto, ProjetoResponseDTO.class);
    }
    
    @Test
    void criarProjeto_DeveLancarBusinessException_QuandoDataFimAnteriorDataInicio() {
        // Given
        projetoRequestDTO.setDataFimPrevista(LocalDate.now().minusDays(1));
        
        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> projetoService.criarProjeto(projetoRequestDTO, "usuario-teste")
        );
        
        assertEquals("Data fim prevista não pode ser anterior à data de início", exception.getMessage());
        verify(projetoRepository, never()).save(any());
    }
    
    @Test
    void buscarPorId_DeveRetornarProjetoResponseDTO_QuandoProjetoExiste() {
        // Given
        when(projetoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(projeto));
        when(modelMapper.map(projeto, ProjetoResponseDTO.class)).thenReturn(projetoResponseDTO);
        
        // When
        ProjetoResponseDTO resultado = projetoService.buscarPorId(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Projeto Teste", resultado.getNome());
        
        verify(projetoRepository).findByIdAndAtivoTrue(1L);
    }
    
    @Test
    void buscarPorId_DeveLancarEntityNotFoundException_QuandoProjetoNaoExiste() {
        // Given
        when(projetoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());
        
        // When & Then
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> projetoService.buscarPorId(1L)
        );
        
        assertEquals("Projeto não encontrado com ID: 1", exception.getMessage());
    }
    
    @Test
    void listarProjetos_DeveRetornarPageDeProjetosResponseDTO() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Projeto> projetos = new PageImpl<>(List.of(projeto));
        
        when(projetoRepository.findProjetosComFiltros(any(), any(), any(), eq(pageable)))
            .thenReturn(projetos);
        when(modelMapper.map(projeto, ProjetoResponseDTO.class)).thenReturn(projetoResponseDTO);
        
        // When
        Page<ProjetoResponseDTO> resultado = projetoService.listarProjetos(
            "Teste", StatusProjeto.PLANEJAMENTO, "Responsável", pageable);
        
        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Projeto Teste", resultado.getContent().get(0).getNome());
    }
    
    @Test
    void alterarStatus_DeveAlterarStatusComSucesso_QuandoTransicaoValida() {
        // Given
        when(projetoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(modelMapper.map(projeto, ProjetoResponseDTO.class)).thenReturn(projetoResponseDTO);
        
        // When
        ProjetoResponseDTO resultado = projetoService.alterarStatus(
            1L, StatusProjeto.EM_ANDAMENTO, "usuario-teste");
        
        // Then
        assertNotNull(resultado);
        verify(projetoRepository).save(projeto);
        assertEquals(StatusProjeto.EM_ANDAMENTO, projeto.getStatus());
    }
    
    @Test
    void alterarStatus_DeveLancarBusinessException_QuandoProjetoCancelado() {
        // Given
        projeto.setStatus(StatusProjeto.CANCELADO);
        when(projetoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(projeto));
        
        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> projetoService.alterarStatus(1L, StatusProjeto.EM_ANDAMENTO, "usuario-teste")
        );
        
        assertEquals("Não é possível alterar status de um projeto cancelado", exception.getMessage());
    }
    
    @Test
    void excluirProjeto_DeveExcluirLogicamente_QuandoStatusPermite() {
        // Given
        when(projetoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        
        // When
        projetoService.excluirProjeto(1L, "usuario-teste");
        
        // Then
        verify(projetoRepository).save(projeto);
        assertFalse(projeto.getAtivo());
        assertEquals("usuario-teste", projeto.getUsuarioAtualizacao());
    }
    
    @Test
    void excluirProjeto_DeveLancarBusinessException_QuandoProjetoEmAndamento() {
        // Given
        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);
        when(projetoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(projeto));
        
        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> projetoService.excluirProjeto(1L, "usuario-teste")
        );
        
        assertEquals("Não é possível excluir um projeto em andamento", exception.getMessage());
    }
}
