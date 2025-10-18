package com.projeto.application.projeto.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Command para criar projeto
 * Segue o padrão CQRS e encapsula dados de entrada
 * Aplica validações no nível de aplicação
 */
public record CriarProjetoCommand(
    @NotBlank(message = "Nome do projeto é obrigatório")
    @Size(max = 200, message = "Nome do projeto não pode ter mais que 200 caracteres")
    String nome,
    
    @Size(max = 1000, message = "Descrição não pode ter mais que 1000 caracteres")
    String descricao,
    
    LocalDate dataInicio,
    
    LocalDate dataFim,
    
    @NotBlank(message = "Responsável é obrigatório")
    @Size(max = 100, message = "Nome do responsável não pode ter mais que 100 caracteres")
    String responsavel,
    
    @NotBlank(message = "Usuário criador é obrigatório")
    String criador
) {
    
    public CriarProjetoCommand {
        // Validações customizadas
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim");
        }
    }
}
