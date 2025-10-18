package com.projeto.management.model.entity;

import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.model.enums.StatusProjeto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Projeto
 * Representa um projeto no sistema de gerenciamento
 */
@Entity
@Table(name = "projeto", indexes = {
    @Index(name = "idx_projeto_nome", columnList = "nome"),
    @Index(name = "idx_projeto_status", columnList = "status"),
    @Index(name = "idx_projeto_responsavel", columnList = "responsavel")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Projeto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome do projeto é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    
    @Column(name = "data_fim_prevista")
    private LocalDate dataFimPrevista;
    
    @Column(name = "data_fim_real")
    private LocalDate dataFimReal;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusProjeto status = StatusProjeto.PLANEJAMENTO;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Prioridade prioridade = Prioridade.MEDIA;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal orcamento;
    
    @Size(max = 255)
    private String responsavel;
    
    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;
    
    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Size(max = 100)
    @Column(name = "usuario_criacao", updatable = false)
    private String usuarioCriacao;
    
    @Size(max = 100)
    @Column(name = "usuario_atualizacao")
    private String usuarioAtualizacao;
    
    @Builder.Default
    private Boolean ativo = true;
    
    // Relacionamento com Tarefas
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Tarefa> tarefas = new ArrayList<>();
    
    // Métodos de convániência
    public void adicionarTarefa(Tarefa tarefa) {
        tarefas.add(tarefa);
        tarefa.setProjeto(this);
    }
    
    public void removerTarefa(Tarefa tarefa) {
        tarefas.remove(tarefa);
        tarefa.setProjeto(null);
    }
    
    /**
     * Calcula o percentual de conclusão do projeto baseado nas tarefas
     */
    public double getPercentualConclusao() {
        if (tarefas == null || tarefas.isEmpty()) {
            return 0.0;
        }
        
        long tarefasConcluidas = tarefas.stream()
            .filter(t -> t.getAtivo() && t.getStatus() == com.projeto.management.model.enums.StatusTarefa.CONCLUIDA)
            .count();
            
        return (double) tarefasConcluidas / tarefas.size() * 100.0;
    }
    
    /**
     * Verifica se o projeto está atrasado
     */
    public boolean isAtrasado() {
        return dataFimPrevista != null && 
               LocalDate.now().isAfter(dataFimPrevista) && 
               status != StatusProjeto.CONCLUIDO;
    }
    
    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (dataAtualizacao == null) {
            dataAtualizacao = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
