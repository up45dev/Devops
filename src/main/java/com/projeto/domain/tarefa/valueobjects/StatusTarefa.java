package com.projeto.domain.tarefa.valueobjects;

import com.projeto.domain.shared.ValueObject;
import java.util.Objects;

/**
 * Value Object para representar o status de uma tarefa
 * Implementa regras de negócio para transições de status
 * Segue princípios DDD e encapsulamento
 */
public record StatusTarefa(String valor) implements ValueObject {
    
    public static final StatusTarefa ABERTA = new StatusTarefa("ABERTA");
    public static final StatusTarefa EM_ANDAMENTO = new StatusTarefa("EM_ANDAMENTO");
    public static final StatusTarefa CONCLUIDA = new StatusTarefa("CONCLUIDA");
    public static final StatusTarefa CANCELADA = new StatusTarefa("CANCELADA");
    public static final StatusTarefa BLOQUEADA = new StatusTarefa("BLOQUEADA");
    
    public StatusTarefa {
        Objects.requireNonNull(valor, "Status da tarefa não pode ser nulo");
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Status da tarefa não pode ser vazio");
        }
        validarStatus(valor);
    }
    
    private void validarStatus(String status) {
        var statusValidos = java.util.Set.of(
            "ABERTA", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA", "BLOQUEADA"
        );
        
        if (!statusValidos.contains(status.toUpperCase())) {
            throw new IllegalArgumentException(
                "Status inválido: " + status + ". Status válidos: " + statusValidos
            );
        }
    }
    
    public static StatusTarefa of(String valor) {
        return new StatusTarefa(valor.toUpperCase());
    }
    
    /**
     * Verifica se é possível alterar para o novo status
     * Implementa regras de negócio para transições
     */
    public boolean podeSerAlteradoPara(StatusTarefa novoStatus) {
        return switch (this.valor) {
            case "ABERTA" -> true; // Pode ir para qualquer status
            case "EM_ANDAMENTO" -> !novoStatus.valor.equals("ABERTA");
            case "BLOQUEADA" -> !novoStatus.valor.equals("CONCLUIDA");
            case "CONCLUIDA", "CANCELADA" -> false; // Estados finais
            default -> false;
        };
    }
    
    public boolean estaAtiva() {
        return "ABERTA".equals(valor) || "EM_ANDAMENTO".equals(valor);
    }
    
    public boolean estaFinalizada() {
        return "CONCLUIDA".equals(valor) || "CANCELADA".equals(valor);
    }
    
    public boolean estaBloqueada() {
        return "BLOQUEADA".equals(valor);
    }
}
