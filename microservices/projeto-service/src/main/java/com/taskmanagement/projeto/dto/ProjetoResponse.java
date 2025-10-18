package com.taskmanagement.projeto.dto;

import com.taskmanagement.projeto.entity.StatusProjeto;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta de projeto
 */
public class ProjetoResponse {
    
    private Long id;
    private String nome;
    private String descricao;
    private StatusProjeto status;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String responsavel;
    private String usuarioCriacao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private boolean atrasado;
    
    // Constructors
    public ProjetoResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public StatusProjeto getStatus() { return status; }
    public void setStatus(StatusProjeto status) { this.status = status; }
    
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    
    public String getUsuarioCriacao() { return usuarioCriacao; }
    public void setUsuarioCriacao(String usuarioCriacao) { this.usuarioCriacao = usuarioCriacao; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    
    public boolean isAtrasado() { return atrasado; }
    public void setAtrasado(boolean atrasado) { this.atrasado = atrasado; }
}