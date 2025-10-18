package com.taskmanagement.projeto.entity;

/**
 * Enum para status do projeto
 */
public enum StatusProjeto {
    PLANEJAMENTO("Planejamento"),
    EM_ANDAMENTO("Em Andamento"),
    PAUSADO("Pausado"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");
    
    private final String descricao;
    
    StatusProjeto(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}