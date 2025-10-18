package com.projeto.domain.projeto;

import com.projeto.domain.shared.AuditInfo;
import com.projeto.domain.shared.Entity;
import com.projeto.domain.projeto.valueobjects.StatusProjeto;
import com.projeto.domain.projeto.events.ProjetoEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root para Projeto
 * Encapsula regras de negócio e gerencia eventos de domínio
 * Segue princípios DDD, SRP e encapsulamento
 */
public class Projeto extends Entity<Long> {
    
    private String nome;
    private String descricao;
    private StatusProjeto status;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String responsavel;
    private AuditInfo auditInfo;
    private final List<ProjetoEvent> eventos = new ArrayList<>();
    
    // Construtor para persistência (package-private)
    Projeto(Long id, String nome, String descricao, StatusProjeto status,
            LocalDate dataInicio, LocalDate dataFim, String responsavel, AuditInfo auditInfo) {
        super(id);
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.responsavel = responsavel;
        this.auditInfo = auditInfo;
        validate();
    }
    
    /**
     * Factory method para criar novo projeto
     * Segue o princípio de criação controlada
     */
    public static Projeto criar(String nome, String descricao, LocalDate dataInicio, 
                               LocalDate dataFim, String responsavel, String criador) {
        var projeto = new Projeto(
            null, // ID será gerado pela infraestrutura
            nome,
            descricao,
            StatusProjeto.PLANEJAMENTO,
            dataInicio,
            dataFim,
            responsavel,
            AuditInfo.criar(criador)
        );
        
        projeto.adicionarEvento(new ProjetoEvent.ProjetoCriado(projeto.getId(), nome));
        return projeto;
    }
    
    /**
     * Inicia o projeto
     * Aplica regras de negócio para transição de status
     */
    public void iniciar(String usuario) {
        if (!status.podeSerAlteradoPara(StatusProjeto.EM_ANDAMENTO)) {
            throw new IllegalStateException(
                "Não é possível iniciar projeto com status: " + status.valor()
            );
        }
        
        this.status = StatusProjeto.EM_ANDAMENTO;
        this.auditInfo = auditInfo.atualizar(usuario);
        adicionarEvento(new ProjetoEvent.ProjetoIniciado(this.id, this.nome));
    }
    
    /**
     * Finaliza o projeto
     * Verifica precondições para finalização
     */
    public void finalizar(String usuario) {
        if (!status.podeSerAlteradoPara(StatusProjeto.CONCLUIDO)) {
            throw new IllegalStateException(
                "Não é possível finalizar projeto com status: " + status.valor()
            );
        }
        
        this.status = StatusProjeto.CONCLUIDO;
        this.auditInfo = auditInfo.atualizar(usuario);
        adicionarEvento(new ProjetoEvent.ProjetoFinalizado(this.id, this.nome));
    }
    
    /**
     * Cancela o projeto
     */
    public void cancelar(String motivo, String usuario) {
        if (status.estaFinalizado()) {
            throw new IllegalStateException(
                "Não é possível cancelar projeto já finalizado"
            );
        }
        
        this.status = StatusProjeto.CANCELADO;
        this.auditInfo = auditInfo.atualizar(usuario);
        adicionarEvento(new ProjetoEvent.ProjetoCancelado(this.id, this.nome, motivo));
    }
    
    /**
     * Atualiza informações do projeto
     * Aplica validações de negócio
     */
    public void atualizar(String nome, String descricao, LocalDate dataFim, 
                         String responsavel, String usuario) {
        if (status.estaFinalizado()) {
            throw new IllegalStateException(
                "Não é possível atualizar projeto finalizado"
            );
        }
        
        this.nome = nome;
        this.descricao = descricao;
        this.dataFim = dataFim;
        this.responsavel = responsavel;
        this.auditInfo = auditInfo.atualizar(usuario);
        
        validate();
        adicionarEvento(new ProjetoEvent.ProjetoAtualizado(this.id, this.nome));
    }
    
    /**
     * Verifica se o projeto está atrasado
     * Regra de negócio para acompanhamento
     */
    public boolean estaAtrasado() {
        if (dataFim == null || status.estaFinalizado()) {
            return false;
        }
        return LocalDate.now().isAfter(dataFim);
    }
    
    @Override
    protected void validate() {
        Objects.requireNonNull(nome, "Nome do projeto é obrigatório");
        Objects.requireNonNull(status, "Status do projeto é obrigatório");
        Objects.requireNonNull(responsavel, "Responsável do projeto é obrigatório");
        
        if (nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do projeto não pode ser vazio");
        }
        
        if (nome.length() > 200) {
            throw new IllegalArgumentException("Nome do projeto não pode ter mais que 200 caracteres");
        }
        
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim");
        }
    }
    
    private void adicionarEvento(ProjetoEvent evento) {
        eventos.add(evento);
    }
    
    public List<ProjetoEvent> getEventos() {
        return List.copyOf(eventos);
    }
    
    public void limparEventos() {
        eventos.clear();
    }
    
    // Getters (imutáveis)
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public StatusProjeto getStatus() { return status; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public String getResponsavel() { return responsavel; }
    public AuditInfo getAuditInfo() { return auditInfo; }
}
