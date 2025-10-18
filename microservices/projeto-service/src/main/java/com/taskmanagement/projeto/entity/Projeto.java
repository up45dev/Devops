package com.taskmanagement.projeto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Projeto para microserviço
 * Extende RepresentationModel para suporte a HATEOAS
 */
@Entity
@Table(name = "projetos")
public class Projeto extends RepresentationModel<Projeto> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 200)
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProjeto status = StatusProjeto.PLANEJAMENTO;
    
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    
    @Column(name = "data_fim")
    private LocalDate dataFim;
    
    @Column(nullable = false)
    private String responsavel;
    
    @Column(name = "usuario_criacao")
    private String usuarioCriacao;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    // Constructors
    public Projeto() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public Projeto(String nome, String descricao, String responsavel, String usuarioCriacao) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.usuarioCriacao = usuarioCriacao;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    // Business methods
    public void iniciar() {
        if (this.status != StatusProjeto.PLANEJAMENTO) {
            throw new IllegalStateException("Projeto deve estar em PLANEJAMENTO para ser iniciado");
        }
        this.status = StatusProjeto.EM_ANDAMENTO;
    }
    
    public void finalizar() {
        if (this.status != StatusProjeto.EM_ANDAMENTO) {
            throw new IllegalStateException("Projeto deve estar EM_ANDAMENTO para ser finalizado");
        }
        this.status = StatusProjeto.CONCLUIDO;
    }
    
    public void cancelar() {
        if (this.status == StatusProjeto.CONCLUIDO) {
            throw new IllegalStateException("Não é possível cancelar projeto já concluído");
        }
        this.status = StatusProjeto.CANCELADO;
    }
    
    public boolean estaAtrasado() {
        return dataFim != null && 
               LocalDate.now().isAfter(dataFim) && 
               status != StatusProjeto.CONCLUIDO;
    }
    
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
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}