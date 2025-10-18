package com.projeto.application.projeto.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Command para atualizar projeto
 * Segue o padrão CQRS
 */
public record AtualizarProjetoCommand(
    @NotNull(message = "ID do projeto é obrigatório")
    Long id,
    
    @NotBlank(message = "Nome do projeto é obrigatório")
    @Size(max = 200, message = "Nome do projeto não pode ter mais que 200 caracteres")
    String nome,
    
    @Size(max = 1000, message = "Descrição não pode ter mais que 1000 caracteres")
    String descricao,
    
    LocalDate dataFim,
    
    @NotBlank(message = "Responsável é obrigatório")
    @Size(max = 100, message = "Nome do responsável não pode ter mais que 100 caracteres")
    String responsavel,
    
    @NotBlank(message = "Usuário é obrigatório")
    String usuario
) {
}
