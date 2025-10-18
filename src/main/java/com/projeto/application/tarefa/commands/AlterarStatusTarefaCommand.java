package com.projeto.application.tarefa.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Command para alterar status de tarefa
 * Segue o padrão CQRS
 */
public record AlterarStatusTarefaCommand(
    @NotNull(message = "ID da tarefa é obrigatório")
    Long id,
    
    @NotBlank(message = "Status é obrigatório")
    String status,
    
    String motivo, // Opcional, usado para bloqueio
    
    @NotBlank(message = "Usuário é obrigatório")
    String usuario
) {
}