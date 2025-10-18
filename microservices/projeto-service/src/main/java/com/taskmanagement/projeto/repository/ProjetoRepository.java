package com.taskmanagement.projeto.repository;

import com.taskmanagement.projeto.entity.Projeto;
import com.taskmanagement.projeto.entity.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade Projeto
 */
@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    
    List<Projeto> findByStatus(StatusProjeto status);
    
    List<Projeto> findByResponsavel(String responsavel);
    
    List<Projeto> findByUsuarioCriacao(String usuarioCriacao);
    
    boolean existsByNome(String nome);
    
    Optional<Projeto> findByNome(String nome);
    
    @Query("SELECT p FROM Projeto p WHERE p.dataFim < :data AND p.status != 'CONCLUIDO'")
    List<Projeto> findProjetosAtrasados(@Param("data") LocalDate data);
    
    @Query("SELECT p FROM Projeto p WHERE p.nome LIKE %:nome% OR p.descricao LIKE %:nome%")
    List<Projeto> findByNomeOrDescricaoContaining(@Param("nome") String nome);
    
    @Query("SELECT COUNT(p) FROM Projeto p WHERE p.status = :status")
    Long countByStatus(@Param("status") StatusProjeto status);
}