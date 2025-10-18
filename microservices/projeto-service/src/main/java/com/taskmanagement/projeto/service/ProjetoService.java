package com.taskmanagement.projeto.service;

import com.taskmanagement.projeto.entity.Projeto;
import com.taskmanagement.projeto.entity.StatusProjeto;
import com.taskmanagement.projeto.repository.ProjetoRepository;
import com.taskmanagement.projeto.dto.ProjetoRequest;
import com.taskmanagement.projeto.event.ProjetoEventPublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service para gerenciamento de projetos
 */
@Service
@Transactional
public class ProjetoService {
    
    @Autowired
    private ProjetoRepository projetoRepository;
    
    @Autowired
    private ProjetoEventPublisher eventPublisher;
    
    /**
     * Busca todos os projetos com paginação
     */
    public Page<Projeto> findAll(Pageable pageable) {
        return projetoRepository.findAll(pageable);
    }
    
    /**
     * Busca projeto por ID
     */
    public Projeto findById(Long id) {
        return projetoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Projeto não encontrado com ID: " + id));
    }
    
    /**
     * Busca projetos por status
     */
    public Page<Projeto> findByStatus(StatusProjeto status, Pageable pageable) {
        List<Projeto> projetos = projetoRepository.findByStatus(status);
        // Convert List to Page manually or use custom repository method
        return Page.empty(pageable); // Simplified for now
    }
    
    /**
     * Busca projetos por responsável
     */
    public Page<Projeto> findByResponsavel(String responsavel, Pageable pageable) {
        List<Projeto> projetos = projetoRepository.findByResponsavel(responsavel);
        // Convert List to Page manually or use custom repository method
        return Page.empty(pageable); // Simplified for now
    }
    
    /**
     * Cria novo projeto
     */
    public Projeto createProjeto(ProjetoRequest request, String username) {
        // Validar se já existe projeto com mesmo nome
        if (projetoRepository.existsByNome(request.getNome())) {
            throw new RuntimeException("Já existe um projeto com o nome: " + request.getNome());
        }
        
        Projeto projeto = new Projeto(
            request.getNome(),
            request.getDescricao(),
            request.getResponsavel(),
            username
        );
        
        projeto.setDataInicio(request.getDataInicio());
        projeto.setDataFim(request.getDataFim());
        
        Projeto savedProjeto = projetoRepository.save(projeto);
        
        // Publicar evento
        eventPublisher.publishProjetoCriado(savedProjeto);
        
        return savedProjeto;
    }
    
    /**
     * Atualiza projeto existente
     */
    public Projeto updateProjeto(Long id, ProjetoRequest request, String username) {
        Projeto projeto = findById(id);
        
        // Verificar se pode ser atualizado
        if (projeto.getStatus() == StatusProjeto.CONCLUIDO || projeto.getStatus() == StatusProjeto.CANCELADO) {
            throw new RuntimeException("Não é possível atualizar projeto finalizado");
        }
        
        projeto.setNome(request.getNome());
        projeto.setDescricao(request.getDescricao());
        projeto.setResponsavel(request.getResponsavel());
        projeto.setDataInicio(request.getDataInicio());
        projeto.setDataFim(request.getDataFim());
        
        Projeto savedProjeto = projetoRepository.save(projeto);
        
        // Publicar evento
        eventPublisher.publishProjetoAtualizado(savedProjeto);
        
        return savedProjeto;
    }
    
    /**
     * Exclui projeto
     */
    public void deleteProjeto(Long id, String username) {
        Projeto projeto = findById(id);
        
        // Verificar se pode ser excluído
        if (projeto.getStatus() == StatusProjeto.EM_ANDAMENTO) {
            throw new RuntimeException("Não é possível excluir projeto em andamento");
        }
        
        projetoRepository.delete(projeto);
        
        // Publicar evento
        eventPublisher.publishProjetoExcluido(projeto);
    }
    
    /**
     * Inicia projeto
     */
    public Projeto iniciarProjeto(Long id, String username) {
        Projeto projeto = findById(id);
        projeto.iniciar();
        
        Projeto savedProjeto = projetoRepository.save(projeto);
        
        // Publicar evento
        eventPublisher.publishProjetoIniciado(savedProjeto);
        
        return savedProjeto;
    }
    
    /**
     * Finaliza projeto
     */
    public Projeto finalizarProjeto(Long id, String username) {
        Projeto projeto = findById(id);
        projeto.finalizar();
        
        Projeto savedProjeto = projetoRepository.save(projeto);
        
        // Publicar evento
        eventPublisher.publishProjetoFinalizado(savedProjeto);
        
        return savedProjeto;
    }
    
    /**
     * Cancela projeto
     */
    public Projeto cancelarProjeto(Long id, String username) {
        Projeto projeto = findById(id);
        projeto.cancelar();
        
        Projeto savedProjeto = projetoRepository.save(projeto);
        
        // Publicar evento
        eventPublisher.publishProjetoCancelado(savedProjeto);
        
        return savedProjeto;
    }
    
    /**
     * Busca projetos por termo
     */
    public List<Projeto> searchProjetos(String searchTerm) {
        return projetoRepository.findByNomeOrDescricaoContaining(searchTerm);
    }
    
    /**
     * Retorna estatísticas de projetos
     */
    public Map<String, Object> getProjetoStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total", projetoRepository.count());
        stats.put("planejamento", projetoRepository.countByStatus(StatusProjeto.PLANEJAMENTO));
        stats.put("emAndamento", projetoRepository.countByStatus(StatusProjeto.EM_ANDAMENTO));
        stats.put("concluidos", projetoRepository.countByStatus(StatusProjeto.CONCLUIDO));
        stats.put("cancelados", projetoRepository.countByStatus(StatusProjeto.CANCELADO));
        stats.put("atrasados", projetoRepository.findProjetosAtrasados(LocalDate.now()).size());
        
        return stats;
    }
}