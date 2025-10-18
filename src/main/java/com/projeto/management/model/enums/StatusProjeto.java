package com.projeto.management.model.enums;

/**
 * Enumeração para Status do Projeto
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
