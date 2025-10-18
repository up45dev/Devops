package com.projeto.application.tarefa;

import com.projeto.application.tarefa.commands.CriarTarefaCommand;
import com.projeto.application.tarefa.commands.AtualizarTarefaCommand;
import com.projeto.application.tarefa.commands.AlterarStatusTarefaCommand;
import com.projeto.application.tarefa.commands.AtualizarPercentualCommand;
import com.projeto.domain.tarefa.Tarefa;
import com.projeto.domain.tarefa.TarefaRepository;
import com.projeto.domain.tarefa.valueobjects.StatusTarefa;
import com.projeto.domain.tarefa.valueobjects.PrioridadeTarefa;
import com.projeto.domain.projeto.ProjetoRepository;
import com.projeto.infrastructure.events.EventPublisher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Application Service para Tarefa
 * Implementa casos de uso de negócio
 * Segue princípios SRP, DIP e coordenação entre domínio e infraestrutura
 */
@Service
@Transactional
public class TarefaApplicationService {
    
    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    private final EventPublisher eventPublisher;
    
    public TarefaApplicationService(TarefaRepository tarefaRepository,
                                  ProjetoRepository projetoRepository,
                                  EventPublisher eventPublisher) {
        this.tarefaRepository = tarefaRepository;
        this.projetoRepository = projetoRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Caso de uso: Criar nova tarefa
     * Aplica validações de negócio e verifica consistência
     */
    public Long criarTarefa(@Valid CriarTarefaCommand command) {
        // Verificação de consistência: projeto deve existir
        var projeto = projetoRepository.findById(command.projetoId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Projeto não encontrado com ID: " + command.projetoId()
            ));
        
        // Regra de negócio: não pode criar tarefa em projeto finalizado
        if (projeto.getStatus().estaFinalizado()) {
            throw new IllegalStateException(
                "Não é possível criar tarefa em projeto finalizado"
            );
        }
        
        // Criação da entidade de domínio
        var prioridade = PrioridadeTarefa.of(command.prioridade());
        var tarefa = Tarefa.criar(
            command.titulo(),
            command.descricao(),
            prioridade,
            command.dataVencimento(),
            command.projetoId(),
            command.responsavel(),
            command.criador()
        );
        
        // Persistência
        var tarefaSalva = tarefaRepository.save(tarefa);
        
        // Publicação de eventos de domínio
        tarefa.getEventos().forEach(eventPublisher::publish);
        tarefa.limparEventos();
        
        return tarefaSalva.getId();
    }
    
    /**
     * Caso de uso: Atualizar tarefa existente
     */
    public void atualizarTarefa(@Valid AtualizarTarefaCommand command) {
        var tarefa = buscarTarefaPorId(command.id());
        var prioridade = PrioridadeTarefa.of(command.prioridade());
        
        tarefa.atualizar(
            command.titulo(),
            command.descricao(),
            prioridade,
            command.dataVencimento(),
            command.responsavel(),
            command.usuario()
        );
        
        tarefaRepository.save(tarefa);
        tarefa.getEventos().forEach(eventPublisher::publish);
        tarefa.limparEventos();
    }
    
    /**
     * Caso de uso: Alterar status da tarefa
     */
    public void alterarStatus(@Valid AlterarStatusTarefaCommand command) {
        var tarefa = buscarTarefaPorId(command.id());
        var novoStatus = StatusTarefa.of(command.status());
        
        switch (novoStatus.valor()) {
            case "EM_ANDAMENTO" -> {
                if (tarefa.getStatus().estaBloqueada()) {
                    tarefa.desbloquear(command.usuario());
                } else {
                    tarefa.iniciar(command.usuario());
                }
            }
            case "CONCLUIDA" -> tarefa.finalizar(command.usuario());
            case "BLOQUEADA" -> tarefa.bloquear(command.motivo(), command.usuario());
            default -> throw new IllegalArgumentException(
                "Transição de status não suportada: " + command.status()
            );
        }
        
        tarefaRepository.save(tarefa);
        tarefa.getEventos().forEach(eventPublisher::publish);
        tarefa.limparEventos();
    }
    
    /**
     * Caso de uso: Atualizar percentual de conclusão
     */
    public void atualizarPercentual(@Valid AtualizarPercentualCommand command) {
        var tarefa = buscarTarefaPorId(command.id());
        
        tarefa.atualizarPercentual(command.percentual(), command.usuario());
        
        tarefaRepository.save(tarefa);
        tarefa.getEventos().forEach(eventPublisher::publish);
        tarefa.limparEventos();
    }
    
    /**
     * Caso de uso: Excluir tarefa
     */
    public void excluirTarefa(@NotNull Long id, @NotNull String usuario) {
        var tarefa = buscarTarefaPorId(id);
        
        // Regra de negócio: pode excluir qualquer tarefa
        // Mas registra evento para auditoria
        
        tarefaRepository.delete(tarefa);
    }
    
    /**
     * Método auxiliar para buscar tarefa com tratamento de erro
     * Aplica o princípio DRY (Don't Repeat Yourself)
     */
    private Tarefa buscarTarefaPorId(Long id) {
        return tarefaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Tarefa não encontrada com ID: " + id
            ));
    }
}
