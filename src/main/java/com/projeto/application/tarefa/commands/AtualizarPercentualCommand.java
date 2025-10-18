package com.projeto.application.tarefa.commands;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Command para atualizar percentual de conclusão
 * Segue o padrão CQRS
 */
public record AtualizarPercentualCommand(
    @NotNull(message = "ID da tarefa é obrigatório")
    Long id,
    
    @NotNull(message = "Percentual é obrigatório")
    @Min(value = 0, message = "Percentual deve ser maior ou igual a 0")
    @Max(value = 100, message = "Percentual deve ser menor ou igual a 100")
    Integer percentual,
    
    @NotBlank(message = "Usuário é obrigatório")
    String usuario
) {
}
