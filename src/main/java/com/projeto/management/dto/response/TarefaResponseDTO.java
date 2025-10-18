package com.projeto.management.dto.response;

import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.model.enums.StatusTarefa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Tarefa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarefaResponseDTO {
    
    private Long id;
    private String titulo;
    private String descricao;
    private StatusTarefa status;
    private Prioridade prioridade;
    private LocalDateTime dataCriacao;
    private LocalDate dataInicio;
    private LocalDate dataFimPrevista;
    private LocalDate dataFimReal;
    private BigDecimal estimativaHoras;
    private BigDecimal horasTrabalhadas;
    private Integer percentualConclusao;
    private String responsavel;
    private String observacoes;
    private LocalDateTime dataAtualizacao;
    private Boolean ativo;
    
    // Informações do projeto
    private Long idProjeto;
    private String nomeProjeto;
    
    // Campos calculados
    private Boolean atrasada;
    private Long diasRestantes;
}