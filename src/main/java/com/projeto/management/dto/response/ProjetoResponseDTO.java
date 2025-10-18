package com.projeto.management.dto.response;

import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.model.enums.StatusProjeto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para Projeto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoResponseDTO {
    
    private Long id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFimPrevista;
    private LocalDate dataFimReal;
    private StatusProjeto status;
    private Prioridade prioridade;
    private BigDecimal orcamento;
    private String responsavel;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String usuarioCriacao;
    private Boolean ativo;
    
    // Campos calculados
    private Double percentualConclusao;
    private Boolean atrasado;
    private Integer totalTarefas;
    private Integer tarefasConcluidas;
    private BigDecimal totalHorasEstimadas;
    private BigDecimal totalHorasTrabalhadas;
    
    // Lista de tarefas (opcional, para endpoints específicos)
    private List<TarefaResponseDTO> tarefas;
}
