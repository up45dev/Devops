package com.projeto.domain.tarefa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.projeto.domain.tarefa.valueobjects.StatusTarefa;
import com.projeto.domain.tarefa.valueobjects.PrioridadeTarefa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface para Tarefa
 * Segue o padrão Repository do DDD
 * Aplica Dependency Inversion Principle (DIP)
 */
public interface TarefaRepository {
    
    /**
     * Salva uma tarefa
     * @param tarefa a tarefa a ser salva
     * @return a tarefa salva com ID gerado
     */
    Tarefa save(Tarefa tarefa);
    
    /**
     * Busca tarefa por ID
     * @param id o ID da tarefa
     * @return Optional contendo a tarefa se encontrada
     */
    Optional<Tarefa> findById(Long id);
    
    /**
     * Lista todas as tarefas com paginação
     * @param pageable informações de paginação
     * @return página de tarefas
     */
    Page<Tarefa> findAll(Pageable pageable);
    
    /**
     * Busca tarefas por projeto
     * @param projetoId o ID do projeto
     * @param pageable informações de paginação
     * @return página de tarefas do projeto
     */
    Page<Tarefa> findByProjetoId(Long projetoId, Pageable pageable);
    
    /**
     * Busca tarefas por status
     * @param status o status das tarefas
     * @param pageable informações de paginação
     * @return página de tarefas com o status especificado
     */
    Page<Tarefa> findByStatus(StatusTarefa status, Pageable pageable);
    
    /**
     * Busca tarefas por responsável
     * @param responsavel o nome do responsável
     * @param pageable informações de paginação
     * @return página de tarefas do responsável
     */
    Page<Tarefa> findByResponsavel(String responsavel, Pageable pageable);
    
    /**
     * Busca tarefas por prioridade
     * @param prioridade a prioridade das tarefas
     * @param pageable informações de paginação
     * @return página de tarefas com a prioridade especificada
     */
    Page<Tarefa> findByPrioridade(PrioridadeTarefa prioridade, Pageable pageable);
    
    /**
     * Busca tarefas atrasadas (data vencimento anterior a hoje e status ativo)
     * @return lista de tarefas atrasadas
     */
    List<Tarefa> findTarefasAtrasadas();
    
    /**
     * Busca tarefas vencendo em determinado período
     * @param dataLimite data limite para vencimento
     * @return lista de tarefas vencendo
     */
    List<Tarefa> findTarefasVencendoAte(LocalDate dataLimite);
    
    /**
     * Busca tarefas de alta prioridade (ALTA ou CRITICA)
     * @param pageable informações de paginação
     * @return página de tarefas de alta prioridade
     */
    Page<Tarefa> findTarefasAltaPrioridade(Pageable pageable);
    
    /**
     * Conta tarefas por status
     * @param status o status para contagem
     * @return número de tarefas com o status
     */
    long countByStatus(StatusTarefa status);
    
    /**
     * Conta tarefas por projeto
     * @param projetoId o ID do projeto
     * @return número de tarefas do projeto
     */
    long countByProjetoId(Long projetoId);
    
    /**
     * Remove uma tarefa
     * @param tarefa a tarefa a ser removida
     */
    void delete(Tarefa tarefa);
}
