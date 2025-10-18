package com.projeto.domain.projeto.valueobjects;

import com.projeto.domain.shared.ValueObject;
import java.util.Objects;

/**
 * Value Object para representar o status de um projeto
 * Garante que apenas valores válidos sejam utilizados
 * Segue o princípio SRP e encapsulamento
 */
public record StatusProjeto(String valor) implements ValueObject {
    
    public static final StatusProjeto PLANEJAMENTO = new StatusProjeto("PLANEJAMENTO");
    public static final StatusProjeto EM_ANDAMENTO = new StatusProjeto("EM_ANDAMENTO");
    public static final StatusProjeto CONCLUIDO = new StatusProjeto("CONCLUIDO");
    public static final StatusProjeto CANCELADO = new StatusProjeto("CANCELADO");
    public static final StatusProjeto PAUSADO = new StatusProjeto("PAUSADO");
    
    public StatusProjeto {
        Objects.requireNonNull(valor, "Status do projeto não pode ser nulo");
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Status do projeto não pode ser vazio");
        }
        validarStatus(valor);
    }
    
    private void validarStatus(String status) {
        var statusValidos = java.util.Set.of(
            "PLANEJAMENTO", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO", "PAUSADO"
        );
        
        if (!statusValidos.contains(status.toUpperCase())) {
            throw new IllegalArgumentException(
                "Status inválido: " + status + ". Status válidos: " + statusValidos
            );
        }
    }
    
    public static StatusProjeto of(String valor) {
        return new StatusProjeto(valor.toUpperCase());
    }
    
    public boolean podeSerAlteradoPara(StatusProjeto novoStatus) {
        return switch (this.valor) {
            case "PLANEJAMENTO" -> !novoStatus.valor.equals("CONCLUIDO");
            case "EM_ANDAMENTO" -> true;
            case "PAUSADO" -> !novoStatus.valor.equals("CONCLUIDO");
            case "CONCLUIDO", "CANCELADO" -> false;
            default -> false;
        };
    }
    
    public boolean estaAtivo() {
        return "EM_ANDAMENTO".equals(valor) || "PLANEJAMENTO".equals(valor);
    }
    
    public boolean estaFinalizado() {
        return "CONCLUIDO".equals(valor) || "CANCELADO".equals(valor);
    }
}
