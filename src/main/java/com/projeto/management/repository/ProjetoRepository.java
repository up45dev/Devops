package com.projeto.management.repository;

import com.projeto.management.model.entity.Projeto;
import com.projeto.management.model.enums.StatusProjeto;
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
 * Repositório para a entidade Projeto
 */
@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    
    /**
     * Busca projetos ativos
     */
    List<Projeto> findByAtivoTrue();
    
    /**
     * Busca projetos por status
     */
    List<Projeto> findByStatusAndAtivoTrue(StatusProjeto status);
    
    /**
     * Busca projetos por prioridade
     */
    List<Projeto> findByPrioridadeAndAtivoTrue(Prioridade prioridade);
    
    /**
     * Busca projetos por responsável
     */
    List<Projeto> findByResponsavelContainingIgnoreCaseAndAtivoTrue(String responsavel);
    
    /**
     * Busca projetos por nome
     */
    List<Projeto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    
    /**
     * Busca projeto ativo por ID
     */
    Optional<Projeto> findByIdAndAtivoTrue(Long id);
    
    /**
     * Busca projetos com data fim prevista vencida
     */
    @Query("SELECT p FROM Projeto p WHERE p.ativo = true AND p.dataFimPrevista < :dataAtual AND p.status != 'CONCLUIDO'")
    List<Projeto> findProjetosAtrasados(@Param("dataAtual") LocalDate dataAtual);
    
    /**
     * Busca projetos com paginação e filtros
     */
    @Query("SELECT p FROM Projeto p WHERE p.ativo = true " +
           "AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:responsavel IS NULL OR LOWER(p.responsavel) LIKE LOWER(CONCAT('%', :responsavel, '%')))")
    Page<Projeto> findProjetosComFiltros(@Param("nome") String nome, 
                                        @Param("status") StatusProjeto status,
                                        @Param("responsavel") String responsavel,
                                        Pageable pageable);
    
    /**
     * Conta projetos por status
     */
    @Query("SELECT p.status, COUNT(p) FROM Projeto p WHERE p.ativo = true GROUP BY p.status")
    List<Object[]> countProjetosPorStatus();
    
    /**
     * Busca projetos com resumo de tarefas
     */
    @Query("SELECT p, COUNT(t), COUNT(CASE WHEN t.status = 'CONCLUIDA' THEN 1 END) " +
           "FROM Projeto p LEFT JOIN p.tarefas t " +
           "WHERE p.ativo = true AND (t.ativo = true OR t IS NULL) " +
           "GROUP BY p.id")
    List<Object[]> findProjetosComResumoTarefas();
}
