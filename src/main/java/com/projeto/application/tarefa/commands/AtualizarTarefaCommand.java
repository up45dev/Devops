package com.projeto.application.tarefa.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Command para atualizar tarefa
 * Segue o padrão CQRS
 */
public record AtualizarTarefaCommand(
    @NotNull(message = "ID da tarefa é obrigatório")
    Long id,
    
    @NotBlank(message = "Título da tarefa é obrigatório")
    @Size(max = 255, message = "Título não pode ter mais que 255 caracteres")
    String titulo,
    
    @Size(max = 2000, message = "Descrição não pode ter mais que 2000 caracteres")
    String descricao,
    
    @NotBlank(message = "Prioridade é obrigatória")
    String prioridade,
    
    LocalDate dataVencimento,
    
    @NotBlank(message = "Responsável é obrigatório")
    @Size(max = 100, message = "Nome do responsável não pode ter mais que 100 caracteres")
    String responsavel,
    
    @NotBlank(message = "Usuário é obrigatório")
    String usuario
) {
}
