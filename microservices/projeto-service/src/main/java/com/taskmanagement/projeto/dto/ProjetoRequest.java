package com.taskmanagement.projeto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * DTO para requisições de projeto
 */
public class ProjetoRequest {
    
    @NotBlank
    @Size(min = 3, max = 200)
    private String nome;
    
    private String descricao;
    
    private LocalDate dataInicio;
    
    private LocalDate dataFim;
    
    @NotBlank
    private String responsavel;
    
    // Constructors
    public ProjetoRequest() {}
    
    public ProjetoRequest(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim, String responsavel) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.responsavel = responsavel;
    }
    
    // Getters and Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
}