package com.projeto.domain.tarefa.valueobjects;

import com.projeto.domain.shared.ValueObject;

/**
 * Value Object para representar a prioridade de uma tarefa
 * Encapsula lógica de negócio relacionada à prioridade
 * Segue princípios DDD
 */
public record PrioridadeTarefa(String valor, int peso) implements ValueObject {
    
    public static final PrioridadeTarefa BAIXA = new PrioridadeTarefa("BAIXA", 1);
    public static final PrioridadeTarefa MEDIA = new PrioridadeTarefa("MEDIA", 2);
    public static final PrioridadeTarefa ALTA = new PrioridadeTarefa("ALTA", 3);
    public static final PrioridadeTarefa CRITICA = new PrioridadeTarefa("CRITICA", 4);
    
    public PrioridadeTarefa {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Prioridade não pode ser nula ou vazia");
        }
        if (peso < 1 || peso > 4) {
            throw new IllegalArgumentException("Peso da prioridade deve estar entre 1 e 4");
        }
        validarPrioridade(valor);
    }
    
    private void validarPrioridade(String prioridade) {
        var prioridadesValidas = java.util.Set.of("BAIXA", "MEDIA", "ALTA", "CRITICA");
        
        if (!prioridadesValidas.contains(prioridade.toUpperCase())) {
            throw new IllegalArgumentException(
                "Prioridade inválida: " + prioridade + ". Prioridades válidas: " + prioridadesValidas
            );
        }
    }
    
    public static PrioridadeTarefa of(String valor) {
        return switch (valor.toUpperCase()) {
            case "BAIXA" -> BAIXA;
            case "MEDIA" -> MEDIA;
            case "ALTA" -> ALTA;
            case "CRITICA" -> CRITICA;
            default -> throw new IllegalArgumentException("Prioridade inválida: " + valor);
        };
    }
    
    public boolean ehMaiorQue(PrioridadeTarefa outra) {
        return this.peso > outra.peso;
    }
    
    public boolean ehCritica() {
        return this.peso == 4;
    }
    
    public boolean ehAlta() {
        return this.peso >= 3;
    }
}
