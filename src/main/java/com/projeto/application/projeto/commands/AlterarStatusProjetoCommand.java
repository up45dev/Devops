package com.projeto.application.projeto.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Command para alterar status de projeto
 * Segue o padrão CQRS
 */
public record AlterarStatusProjetoCommand(
    @NotNull(message = "ID do projeto é obrigatório")
    Long id,
    
    @NotBlank(message = "Status é obrigatório")
    String status,
    
    String motivo, // Opcional, usado para cancelamento
    
    @NotBlank(message = "Usuário é obrigatório")
    String usuario
) {
}
