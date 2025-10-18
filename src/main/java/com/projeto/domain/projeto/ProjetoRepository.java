package com.projeto.domain.projeto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.projeto.domain.projeto.valueobjects.StatusProjeto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface para Projeto
 * Segue o padrão Repository do DDD
 * Aplica Dependency Inversion Principle (DIP)
 */
public interface ProjetoRepository {
    
    /**
     * Salva um projeto
     * @param projeto o projeto a ser salvo
     * @return o projeto salvo com ID gerado
     */
    Projeto save(Projeto projeto);
    
    /**
     * Busca projeto por ID
     * @param id o ID do projeto
     * @return Optional contendo o projeto se encontrado
     */
    Optional<Projeto> findById(Long id);
    
    /**
     * Lista todos os projetos com paginação
     * @param pageable informações de paginação
     * @return página de projetos
     */
    Page<Projeto> findAll(Pageable pageable);
    
    /**
     * Busca projetos por status
     * @param status o status dos projetos
     * @param pageable informações de paginação
     * @return página de projetos com o status especificado
     */
    Page<Projeto> findByStatus(StatusProjeto status, Pageable pageable);
    
    /**
     * Busca projetos por responsável
     * @param responsavel o nome do responsável
     * @param pageable informações de paginação
     * @return página de projetos do responsável
     */
    Page<Projeto> findByResponsavel(String responsavel, Pageable pageable);
    
    /**
     * Busca projetos atrasados (data fim anterior a hoje e status ativo)
     * @return lista de projetos atrasados
     */
    List<Projeto> findProjetosAtrasados();
    
    /**
     * Busca projetos vencendo em determinado período
     * @param dataLimite data limite para vencimento
     * @return lista de projetos vencendo
     */
    List<Projeto> findProjetosVencendoAte(LocalDate dataLimite);
    
    /**
     * Conta projetos por status
     * @param status o status para contagem
     * @return número de projetos com o status
     */
    long countByStatus(StatusProjeto status);
    
    /**
     * Verifica se existe projeto com o nome
     * @param nome o nome do projeto
     * @return true se existe projeto com o nome
     */
    boolean existsByNome(String nome);
    
    /**
     * Remove um projeto
     * @param projeto o projeto a ser removido
     */
    void delete(Projeto projeto);
}
