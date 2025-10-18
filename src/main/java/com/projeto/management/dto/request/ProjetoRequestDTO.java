package com.projeto.management.dto.request;

import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.model.enums.StatusProjeto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para criação de Projeto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoRequestDTO {
    
    @NotBlank(message = "Nome do projeto é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;
    
    private String descricao;
    
    private LocalDate dataInicio;
    
    @Future(message = "Data fim prevista deve ser futura")
    private LocalDate dataFimPrevista;
    
    private StatusProjeto status;
    
    private Prioridade prioridade;
    
    @DecimalMin(value = "0.0", message = "Orçamento deve ser positivo")
    private BigDecimal orcamento;
    
    @Size(max = 255, message = "Nome do responsável deve ter no máximo 255 caracteres")
    private String responsavel;
}
