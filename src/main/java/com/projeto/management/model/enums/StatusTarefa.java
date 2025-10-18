package com.projeto.management.model.enums;

/**
 * Enumeração para Status da Tarefa
 */
public enum StatusTarefa {
    ABERTA("Aberta"),
    EM_ANDAMENTO("Em Andamento"),
    EM_REVISAO("Em Revisão"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");
    
    private final String descricao;
    
    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
