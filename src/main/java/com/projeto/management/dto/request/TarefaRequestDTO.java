package com.projeto.management.dto.request;

import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.model.enums.StatusTarefa;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para criação de Tarefa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarefaRequestDTO {
    
    @NotBlank(message = "Título da tarefa é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String titulo;
    
    private String descricao;
    
    private StatusTarefa status;
    
    private Prioridade prioridade;
    
    private LocalDate dataInicio;
    
    private LocalDate dataFimPrevista;
    
    @DecimalMin(value = "0.0", message = "Estimativa de horas deve ser positiva")
    private BigDecimal estimativaHoras;
    
    @DecimalMin(value = "0.0", message = "Horas trabalhadas deve ser positiva")
    private BigDecimal horasTrabalhadas;
    
    @Min(value = 0, message = "Percentual deve ser entre 0 e 100")
    @Max(value = 100, message = "Percentual deve ser entre 0 e 100")
    private Integer percentualConclusao;
    
    @Size(max = 255, message = "Nome do responsável deve ter no máximo 255 caracteres")
    private String responsavel;
    
    private String observacoes;
    
    @NotNull(message = "ID do projeto é obrigatório")
    private Long idProjeto;
}
