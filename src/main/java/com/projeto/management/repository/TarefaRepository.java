package com.projeto.management.repository;

import com.projeto.management.model.entity.Tarefa;
import com.projeto.management.model.enums.StatusTarefa;
import com.projeto.management.model.enums.Prioridade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade Tarefa
 */
@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    
    /**
     * Busca tarefas ativas
     */
    List<Tarefa> findByAtivoTrue();
    
    /**
     * Busca tarefas por projeto
     */
    List<Tarefa> findByProjetoIdAndAtivoTrue(Long projetoId);
    
    /**
     * Busca tarefas por status
     */
    List<Tarefa> findByStatusAndAtivoTrue(StatusTarefa status);
    
    /**
     * Busca tarefas por responsável
     */
    List<Tarefa> findByResponsavelContainingIgnoreCaseAndAtivoTrue(String responsavel);
    
    /**
     * Busca tarefa ativa por ID
     */
    Optional<Tarefa> findByIdAndAtivoTrue(Long id);
    
    /**
     * Busca tarefas atrasadas
     */
    @Query("SELECT t FROM Tarefa t WHERE t.ativo = true " +
           "AND t.dataFimPrevista < :dataAtual " +
           "AND t.status NOT IN ('CONCLUIDA', 'CANCELADA')")
    List<Tarefa> findTarefasAtrasadas(@Param("dataAtual") LocalDate dataAtual);
    
    /**
     * Busca tarefas por projeto com paginação
     */
    Page<Tarefa> findByProjetoIdAndAtivoTrueOrderByPrioridadeDescDataFimPrevistaAsc(Long projetoId, Pageable pageable);
    
    /**
     * Busca tarefas com filtros
     */
    @Query("SELECT t FROM Tarefa t WHERE t.ativo = true " +
           "AND (:projetoId IS NULL OR t.projeto.id = :projetoId) " +
           "AND (:titulo IS NULL OR LOWER(t.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:responsavel IS NULL OR LOWER(t.responsavel) LIKE LOWER(CONCAT('%', :responsavel, '%'))) " +
           "AND (:prioridade IS NULL OR t.prioridade = :prioridade)")
    Page<Tarefa> findTarefasComFiltros(@Param("projetoId") Long projetoId,
                                      @Param("titulo") String titulo,
                                      @Param("status") StatusTarefa status,
                                      @Param("responsavel") String responsavel,
                                      @Param("prioridade") Prioridade prioridade,
                                      Pageable pageable);
    
    /**
     * Conta tarefas por status de um projeto
     */
    @Query("SELECT t.status, COUNT(t) FROM Tarefa t " +
           "WHERE t.projeto.id = :projetoId AND t.ativo = true " +
           "GROUP BY t.status")
    List<Object[]> countTarefasPorStatusPorProjeto(@Param("projetoId") Long projetoId);
    
    /**
     * Busca tarefas que vencem em X dias
     */
    @Query("SELECT t FROM Tarefa t WHERE t.ativo = true " +
           "AND t.dataFimPrevista BETWEEN :dataAtual AND :dataLimite " +
           "AND t.status NOT IN ('CONCLUIDA', 'CANCELADA')")
    List<Tarefa> findTarefasVencendoEm(@Param("dataAtual") LocalDate dataAtual, 
                                      @Param("dataLimite") LocalDate dataLimite);
    
    /**
     * Busca tarefas de alta prioridade em aberto
     */
    @Query("SELECT t FROM Tarefa t WHERE t.ativo = true " +
           "AND t.prioridade IN ('ALTA', 'CRITICA') " +
           "AND t.status NOT IN ('CONCLUIDA', 'CANCELADA')")
    List<Tarefa> findTarefasAltaPrioridadeEmAberto();
    
    /**
     * Calcula estatísticas de um projeto
     */
    @Query("SELECT " +
           "COUNT(t), " +
           "COUNT(CASE WHEN t.status = 'CONCLUIDA' THEN 1 END), " +
           "SUM(COALESCE(t.estimativaHoras, 0)), " +
           "SUM(COALESCE(t.horasTrabalhadas, 0)) " +
           "FROM Tarefa t WHERE t.projeto.id = :projetoId AND t.ativo = true")
    Object[] getEstatisticasProjeto(@Param("projetoId") Long projetoId);
}
