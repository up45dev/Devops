package com.projeto.domain.tarefa;

import com.projeto.domain.shared.AuditInfo;
import com.projeto.domain.shared.Entity;
import com.projeto.domain.tarefa.valueobjects.StatusTarefa;
import com.projeto.domain.tarefa.valueobjects.PrioridadeTarefa;
import com.projeto.domain.tarefa.events.TarefaEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root para Tarefa
 * Encapsula regras de negócio e gerencia eventos de domínio
 * Segue princípios DDD, SRP e encapsulamento
 */
public class Tarefa extends Entity<Long> {
    
    private String titulo;
    private String descricao;
    private StatusTarefa status;
    private PrioridadeTarefa prioridade;
    private LocalDate dataVencimento;
    private Integer percentualConclusao;
    private Long projetoId;
    private String responsavel;
    private AuditInfo auditInfo;
    private final List<TarefaEvent> eventos = new ArrayList<>();
    
    // Construtor para persistência (package-private)
    Tarefa(Long id, String titulo, String descricao, StatusTarefa status,
           PrioridadeTarefa prioridade, LocalDate dataVencimento, Integer percentualConclusao,
           Long projetoId, String responsavel, AuditInfo auditInfo) {
        super(id);
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.prioridade = prioridade;
        this.dataVencimento = dataVencimento;
        this.percentualConclusao = percentualConclusao;
        this.projetoId = projetoId;
        this.responsavel = responsavel;
        this.auditInfo = auditInfo;
        validate();
    }
    
    /**
     * Factory method para criar nova tarefa
     * Segue o princípio de criação controlada
     */
    public static Tarefa criar(String titulo, String descricao, PrioridadeTarefa prioridade,
                              LocalDate dataVencimento, Long projetoId, String responsavel, String criador) {
        var tarefa = new Tarefa(
            null, // ID será gerado pela infraestrutura
            titulo,
            descricao,
            StatusTarefa.ABERTA,
            prioridade,
            dataVencimento,
            0, // Percentual inicial
            projetoId,
            responsavel,
            AuditInfo.criar(criador)
        );
        
        tarefa.adicionarEvento(new TarefaEvent.TarefaCriada(tarefa.getId(), titulo, projetoId));
        return tarefa;
    }
    
    /**
     * Inicia o trabalho na tarefa
     * Aplica regras de negócio para transição de status
     */
    public void iniciar(String usuario) {
        if (!status.podeSerAlteradoPara(StatusTarefa.EM_ANDAMENTO)) {
            throw new IllegalStateException(
                "Não é possível iniciar tarefa com status: " + status.valor()
            );
        }
        
        this.status = StatusTarefa.EM_ANDAMENTO;
        this.auditInfo = auditInfo.atualizar(usuario);
        adicionarEvento(new TarefaEvent.TarefaIniciada(this.id, this.titulo, this.projetoId));
    }
    
    /**
     * Finaliza a tarefa
     * Aplica regras de negócio e atualiza percentual
     */
    public void finalizar(String usuario) {
        if (!status.podeSerAlteradoPara(StatusTarefa.CONCLUIDA)) {
            throw new IllegalStateException(
                "Não é possível finalizar tarefa com status: " + status.valor()
            );
        }
        
        this.status = StatusTarefa.CONCLUIDA;
        this.percentualConclusao = 100;
        this.auditInfo = auditInfo.atualizar(usuario);
        adicionarEvento(new TarefaEvent.TarefaFinalizada(this.id, this.titulo, this.projetoId));
    }
    
    /**
     * Bloqueia a tarefa
     * Utilizado quando há impedimentos
     */
    public void bloquear(String motivo, String usuario) {
        if (!status.estaAtiva()) {
            throw new IllegalStateException(
                "Apenas tarefas ativas podem ser bloqueadas"
            );
        }
        
        this.status = StatusTarefa.BLOQUEADA;
        this.auditInfo = auditInfo.atualizar(usuario);
        adicionarEvento(new TarefaEvent.TarefaBloqueada(this.id, this.titulo, motivo));
    }
    
    /**
     * Desbloqueia a tarefa
     */
    public void desbloquear(String usuario) {
        if (!status.estaBloqueada()) {
            throw new IllegalStateException("Tarefa não está bloqueada");
        }
        
        this.status = StatusTarefa.EM_ANDAMENTO;
        this.auditInfo = auditInfo.atualizar(usuario);
        adicionarEvento(new TarefaEvent.TarefaDesbloqueada(this.id, this.titulo));
    }
    
    /**
     * Atualiza o percentual de conclusão
     * Aplica regras de negócio
     */
    public void atualizarPercentual(Integer novoPercentual, String usuario) {
        if (status.estaFinalizada()) {
            throw new IllegalStateException(
                "Não é possível atualizar percentual de tarefa finalizada"
            );
        }
        
        if (novoPercentual < 0 || novoPercentual > 100) {
            throw new IllegalArgumentException(
                "Percentual deve estar entre 0 e 100"
            );
        }
        
        var percentualAnterior = this.percentualConclusao;
        this.percentualConclusao = novoPercentual;
        this.auditInfo = auditInfo.atualizar(usuario);
        
        // Auto-finalizar se chegou a 100%
        if (novoPercentual == 100 && status.estaAtiva()) {
            this.status = StatusTarefa.CONCLUIDA;
            adicionarEvento(new TarefaEvent.TarefaFinalizada(this.id, this.titulo, this.projetoId));
        } else {
            adicionarEvento(new TarefaEvent.PercentualAtualizado(
                this.id, this.titulo, percentualAnterior, novoPercentual
            ));
        }
    }
    
    /**
     * Atualiza informações da tarefa
     */
    public void atualizar(String titulo, String descricao, PrioridadeTarefa prioridade,
                         LocalDate dataVencimento, String responsavel, String usuario) {
        if (status.estaFinalizada()) {
            throw new IllegalStateException(
                "Não é possível atualizar tarefa finalizada"
            );
        }
        
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.dataVencimento = dataVencimento;
        this.responsavel = responsavel;
        this.auditInfo = auditInfo.atualizar(usuario);
        
        validate();
        adicionarEvento(new TarefaEvent.TarefaAtualizada(this.id, this.titulo));
    }
    
    /**
     * Verifica se a tarefa está atrasada
     * Regra de negócio para acompanhamento
     */
    public boolean estaAtrasada() {
        if (dataVencimento == null || status.estaFinalizada()) {
            return false;
        }
        return LocalDate.now().isAfter(dataVencimento);
    }
    
    /**
     * Verifica se a tarefa está vencendo em breve
     */
    public boolean estaVencendoEm(int dias) {
        if (dataVencimento == null || status.estaFinalizada()) {
            return false;
        }
        return LocalDate.now().plusDays(dias).isAfter(dataVencimento) &&
               !LocalDate.now().isAfter(dataVencimento);
    }
    
    @Override
    protected void validate() {
        Objects.requireNonNull(titulo, "Título da tarefa é obrigatório");
        Objects.requireNonNull(status, "Status da tarefa é obrigatório");
        Objects.requireNonNull(prioridade, "Prioridade da tarefa é obrigatória");
        Objects.requireNonNull(projetoId, "Projeto da tarefa é obrigatório");
        Objects.requireNonNull(responsavel, "Responsável da tarefa é obrigatório");
        
        if (titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título da tarefa não pode ser vazio");
        }
        
        if (titulo.length() > 255) {
            throw new IllegalArgumentException("Título da tarefa não pode ter mais que 255 caracteres");
        }
        
        if (percentualConclusao != null && (percentualConclusao < 0 || percentualConclusao > 100)) {
            throw new IllegalArgumentException("Percentual deve estar entre 0 e 100");
        }
    }
    
    private void adicionarEvento(TarefaEvent evento) {
        eventos.add(evento);
    }
    
    public List<TarefaEvent> getEventos() {
        return List.copyOf(eventos);
    }
    
    public void limparEventos() {
        eventos.clear();
    }
    
    // Getters (imutáveis)
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public StatusTarefa getStatus() { return status; }
    public PrioridadeTarefa getPrioridade() { return prioridade; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public Integer getPercentualConclusao() { return percentualConclusao; }
    public Long getProjetoId() { return projetoId; }
    public String getResponsavel() { return responsavel; }
    public AuditInfo getAuditInfo() { return auditInfo; }
}
