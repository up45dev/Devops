package com.projeto.management.model.entity;

import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.model.enums.StatusTarefa;
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

/**
 * Entidade Tarefa
 * Representa uma tarefa dentro de um projeto
 */
@Entity
@Table(name = "tarefa", indexes = {
    @Index(name = "idx_tarefa_titulo", columnList = "titulo"),
    @Index(name = "idx_tarefa_status", columnList = "status"),
    @Index(name = "idx_tarefa_projeto", columnList = "id_projeto"),
    @Index(name = "idx_tarefa_responsavel", columnList = "responsavel"),
    @Index(name = "idx_tarefa_prioridade", columnList = "prioridade")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Título da tarefa é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusTarefa status = StatusTarefa.ABERTA;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Prioridade prioridade = Prioridade.MEDIA;
    
    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    
    @Column(name = "data_fim_prevista")
    private LocalDate dataFimPrevista;
    
    @Column(name = "data_fim_real")
    private LocalDate dataFimReal;
    
    @DecimalMin(value = "0.0", message = "Estimativa de horas deve ser positiva")
    @Column(name = "estimativa_horas", precision = 8, scale = 2)
    private BigDecimal estimativaHoras;
    
    @DecimalMin(value = "0.0", message = "Horas trabalhadas deve ser positiva")
    @Column(name = "horas_trabalhadas", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal horasTrabalhadas = BigDecimal.ZERO;
    
    @Min(value = 0, message = "Percentual deve ser entre 0 e 100")
    @Max(value = 100, message = "Percentual deve ser entre 0 e 100")
    @Column(name = "percentual_conclusao")
    @Builder.Default
    private Integer percentualConclusao = 0;
    
    @Size(max = 255)
    private String responsavel;
    
    @Column(columnDefinition = "TEXT")
    private String observacoes;
    
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
    
    // Relacionamento com Projeto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projeto", nullable = false)
    @NotNull(message = "Projeto é obrigatório")
    private Projeto projeto;
    
    /**
     * Verifica se a tarefa está atrasada
     */
    public boolean isAtrasada() {
        return dataFimPrevista != null && 
               LocalDate.now().isAfter(dataFimPrevista) && 
               status != StatusTarefa.CONCLUIDA && 
               status != StatusTarefa.CANCELADA;
    }
    
    /**
     * Calcula o tempo restante em dias
     */
    public long getDiasRestantes() {
        if (dataFimPrevista == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dataFimPrevista);
    }
    
    /**
     * Atualiza o percentual baseado no status
     */
    public void atualizarPercentualPorStatus() {
        switch (status) {
            case ABERTA:
                this.percentualConclusao = 0;
                break;
            case EM_ANDAMENTO:
                if (this.percentualConclusao == 0) {
                    this.percentualConclusao = 10;
                }
                break;
            case EM_REVISAO:
                if (this.percentualConclusao < 90) {
                    this.percentualConclusao = 90;
                }
                break;
            case CONCLUIDA:
                this.percentualConclusao = 100;
                if (this.dataFimReal == null) {
                    this.dataFimReal = LocalDate.now();
                }
                break;
            case CANCELADA:
                // Mantém o percentual atual
                break;
        }
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
        atualizarPercentualPorStatus();
    }
}
