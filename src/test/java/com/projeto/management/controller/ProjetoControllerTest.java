package com.projeto.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.management.dto.request.ProjetoRequestDTO;
import com.projeto.management.dto.response.ProjetoResponseDTO;
import com.projeto.management.model.enums.StatusProjeto;
import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.service.ProjetoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para ProjetoController
 */
@WebMvcTest(ProjetoController.class)
class ProjetoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProjetoService projetoService;
    
    @Test
    void criarProjeto_DeveRetornar201_QuandoDadosValidos() throws Exception {
        // Given
        ProjetoRequestDTO requestDTO = ProjetoRequestDTO.builder()
            .nome("Projeto Teste")
            .descricao("Descrição teste")
            .prioridade(Prioridade.ALTA)
            .responsavel("Responsável")
            .orcamento(new BigDecimal("10000.00"))
            .dataInicio(LocalDate.now())
            .dataFimPrevista(LocalDate.now().plusDays(30))
            .build();
        
        ProjetoResponseDTO responseDTO = ProjetoResponseDTO.builder()
            .id(1L)
            .nome("Projeto Teste")
            .status(StatusProjeto.PLANEJAMENTO)
            .build();
        
        when(projetoService.criarProjeto(any(ProjetoRequestDTO.class), anyString()))
            .thenReturn(responseDTO);
        
        // When & Then
        mockMvc.perform(post("/api/projetos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
                .header("X-Usuario", "teste"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Projeto Teste"))
                .andExpect(jsonPath("$.status").value("PLANEJAMENTO"));
    }
    
    @Test
    void criarProjeto_DeveRetornar400_QuandoNomeVazio() throws Exception {
        // Given
        ProjetoRequestDTO requestDTO = ProjetoRequestDTO.builder()
            .nome("") // Nome vazio
            .descricao("Descrição teste")
            .build();
        
        // When & Then
        mockMvc.perform(post("/api/projetos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void buscarPorId_DeveRetornar200_QuandoProjetoExiste() throws Exception {
        // Given
        ProjetoResponseDTO responseDTO = ProjetoResponseDTO.builder()
            .id(1L)
            .nome("Projeto Teste")
            .status(StatusProjeto.PLANEJAMENTO)
            .build();
        
        when(projetoService.buscarPorId(1L)).thenReturn(responseDTO);
        
        // When & Then
        mockMvc.perform(get("/api/projetos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Projeto Teste"));
    }
    
    @Test
    void listarProjetos_DeveRetornar200_ComPaginacao() throws Exception {
        // Given
        ProjetoResponseDTO responseDTO = ProjetoResponseDTO.builder()
            .id(1L)
            .nome("Projeto Teste")
            .status(StatusProjeto.PLANEJAMENTO)
            .build();
        
        Page<ProjetoResponseDTO> page = new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 20), 1);
        
        when(projetoService.listarProjetos(any(), any(), any(), any()))
            .thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/projetos")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    void alterarStatus_DeveRetornar200_QuandoStatusValido() throws Exception {
        // Given
        ProjetoResponseDTO responseDTO = ProjetoResponseDTO.builder()
            .id(1L)
            .nome("Projeto Teste")
            .status(StatusProjeto.EM_ANDAMENTO)
            .build();
        
        when(projetoService.alterarStatus(eq(1L), eq(StatusProjeto.EM_ANDAMENTO), anyString()))
            .thenReturn(responseDTO);
        
        // When & Then
        mockMvc.perform(patch("/api/projetos/1/status")
                .param("status", "EM_ANDAMENTO")
                .header("X-Usuario", "teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }
    
    @Test
    void excluirProjeto_DeveRetornar204_QuandoExclusaoComSucesso() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/projetos/1")
                .header("X-Usuario", "teste"))
                .andExpect(status().isNoContent());
    }
}
