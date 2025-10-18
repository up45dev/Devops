package com.projeto.management.service;

import com.projeto.management.dto.request.TarefaRequestDTO;
import com.projeto.management.dto.response.TarefaResponseDTO;
import com.projeto.management.exception.EntityNotFoundException;
import com.projeto.management.exception.BusinessException;
import com.projeto.management.model.entity.Projeto;
import com.projeto.management.model.entity.Tarefa;
import com.projeto.management.model.enums.StatusTarefa;
import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.repository.TarefaRepository;
import com.projeto.management.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de Tarefas
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TarefaService {
    
    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    private final ModelMapper modelMapper;
    
    /**
     * Cria uma nova tarefa
     */
    public TarefaResponseDTO criarTarefa(TarefaRequestDTO requestDTO, String usuarioLogado) {
        log.info("Criando nova tarefa: {} para projeto ID: {}", requestDTO.getTitulo(), requestDTO.getIdProjeto());
        
        // Verifica se o projeto existe
        Projeto projeto = projetoRepository.findByIdAndAtivoTrue(requestDTO.getIdProjeto())
            .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + requestDTO.getIdProjeto()));
        
        // Validações de negócio
        validarDatasTarefa(requestDTO.getDataInicio(), requestDTO.getDataFimPrevista());
        
        Tarefa tarefa = modelMapper.map(requestDTO, Tarefa.class);
        tarefa.setProjeto(projeto);
        tarefa.setUsuarioCriacao(usuarioLogado);
        tarefa.setUsuarioAtualizacao(usuarioLogado);
        
        // Define valores padrão se não informados
        if (tarefa.getStatus() == null) {
            tarefa.setStatus(StatusTarefa.ABERTA);
        }
        if (tarefa.getPrioridade() == null) {
            tarefa.setPrioridade(Prioridade.MEDIA);
        }
        if (tarefa.getPercentualConclusao() == null) {
            tarefa.setPercentualConclusao(0);
        }
        
        Tarefa tarefaSalva = tarefaRepository.save(tarefa);
        log.info("Tarefa criada com ID: {}", tarefaSalva.getId());
        
        return converterParaResponseDTO(tarefaSalva);
    }
    
    /**
     * Busca tarefa por ID
     */
    @Transactional(readOnly = true)
    public TarefaResponseDTO buscarPorId(Long id) {
        log.debug("Buscando tarefa por ID: {}", id);
        
        Tarefa tarefa = tarefaRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
            
        return converterParaResponseDTO(tarefa);
    }
    
    /**
     * Lista tarefas com paginação e filtros
     */
    @Transactional(readOnly = true)
    public Page<TarefaResponseDTO> listarTarefas(Long projetoId, String titulo, StatusTarefa status,
                                               String responsavel, Prioridade prioridade, Pageable pageable) {
        log.debug("Listando tarefas com filtros - Projeto: {}, Título: {}, Status: {}", 
                 projetoId, titulo, status);
        
        Page<Tarefa> tarefas = tarefaRepository.findTarefasComFiltros(
            projetoId, titulo, status, responsavel, prioridade, pageable);
        
        return tarefas.map(this::converterParaResponseDTO);
    }
    
    /**
     * Lista tarefas por projeto
     */
    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> listarTarefasPorProjeto(Long projetoId) {
        log.debug("Listando tarefas do projeto: {}", projetoId);
        
        List<Tarefa> tarefas = tarefaRepository.findByProjetoIdAndAtivoTrue(projetoId);
        
        return tarefas.stream()
            .map(this::converterParaResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Atualiza uma tarefa
     */
    public TarefaResponseDTO atualizarTarefa(Long id, TarefaRequestDTO requestDTO, String usuarioLogado) {
        log.info("Atualizando tarefa ID: {}", id);
        
        Tarefa tarefa = tarefaRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        
        // Se mudou o projeto, valida se existe
        if (!tarefa.getProjeto().getId().equals(requestDTO.getIdProjeto())) {
            Projeto novoProjeto = projetoRepository.findByIdAndAtivoTrue(requestDTO.getIdProjeto())
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + requestDTO.getIdProjeto()));
            tarefa.setProjeto(novoProjeto);
        }
        
        // Validações de negócio
        validarDatasTarefa(requestDTO.getDataInicio(), requestDTO.getDataFimPrevista());
        
        // Atualiza os campos
        modelMapper.map(requestDTO, tarefa);
        tarefa.setUsuarioAtualizacao(usuarioLogado);
        
        Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);
        log.info("Tarefa atualizada: {}", tarefaAtualizada.getId());
        
        return converterParaResponseDTO(tarefaAtualizada);
    }
    
    /**
     * Exclui logicamente uma tarefa
     */
    public void excluirTarefa(Long id, String usuarioLogado) {
        log.info("Excluindo tarefa ID: {}", id);
        
        Tarefa tarefa = tarefaRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        
        tarefa.setAtivo(false);
        tarefa.setUsuarioAtualizacao(usuarioLogado);
        tarefaRepository.save(tarefa);
        
        log.info("Tarefa excluída logicamente: {}", id);
    }
    
    /**
     * Altera status da tarefa
     */
    public TarefaResponseDTO alterarStatus(Long id, StatusTarefa novoStatus, String usuarioLogado) {
        log.info("Alterando status da tarefa {} para {}", id, novoStatus);
        
        Tarefa tarefa = tarefaRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        
        StatusTarefa statusAnterior = tarefa.getStatus();
        
        tarefa.setStatus(novoStatus);
        tarefa.setUsuarioAtualizacao(usuarioLogado);
        
        // Atualiza percentual automaticamente
        tarefa.atualizarPercentualPorStatus();
        
        Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);
        log.info("Status alterado de {} para {}", statusAnterior, novoStatus);
        
        return converterParaResponseDTO(tarefaAtualizada);
    }
    
    /**
     * Atualiza percentual de conclusão
     */
    public TarefaResponseDTO atualizarPercentual(Long id, Integer percentual, String usuarioLogado) {
        log.info("Atualizando percentual da tarefa {} para {}%", id, percentual);
        
        if (percentual < 0 || percentual > 100) {
            throw new BusinessException("Percentual deve estar entre 0 e 100");
        }
        
        Tarefa tarefa = tarefaRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + id));
        
        tarefa.setPercentualConclusao(percentual);
        tarefa.setUsuarioAtualizacao(usuarioLogado);
        
        // Atualiza status baseado no percentual
        if (percentual == 100 && tarefa.getStatus() != StatusTarefa.CONCLUIDA) {
            tarefa.setStatus(StatusTarefa.CONCLUIDA);
            tarefa.setDataFimReal(LocalDate.now());
        } else if (percentual > 0 && percentual < 100 && tarefa.getStatus() == StatusTarefa.ABERTA) {
            tarefa.setStatus(StatusTarefa.EM_ANDAMENTO);
        }
        
        Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);
        
        return converterParaResponseDTO(tarefaAtualizada);
    }
    
    /**
     * Busca tarefas atrasadas
     */
    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> buscarTarefasAtrasadas() {
        log.debug("Buscando tarefas atrasadas");
        
        List<Tarefa> tarefas = tarefaRepository.findTarefasAtrasadas(LocalDate.now());
        
        return tarefas.stream()
            .map(this::converterParaResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca tarefas que vencem em X dias
     */
    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> buscarTarefasVencendoEm(int dias) {
        log.debug("Buscando tarefas que vencem em {} dias", dias);
        
        LocalDate dataAtual = LocalDate.now();
        LocalDate dataLimite = dataAtual.plusDays(dias);
        
        List<Tarefa> tarefas = tarefaRepository.findTarefasVencendoEm(dataAtual, dataLimite);
        
        return tarefas.stream()
            .map(this::converterParaResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca tarefas de alta prioridade em aberto
     */
    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> buscarTarefasAltaPrioridade() {
        log.debug("Buscando tarefas de alta prioridade em aberto");
        
        List<Tarefa> tarefas = tarefaRepository.findTarefasAltaPrioridadeEmAberto();
        
        return tarefas.stream()
            .map(this::converterParaResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca tarefas por responsável
     */
    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> buscarPorResponsavel(String responsavel) {
        log.debug("Buscando tarefas por responsável: {}", responsavel);
        
        List<Tarefa> tarefas = tarefaRepository.findByResponsavelContainingIgnoreCaseAndAtivoTrue(responsavel);
        
        return tarefas.stream()
            .map(this::converterParaResponseDTO)
            .collect(Collectors.toList());
    }
    
    // Métodos privados de apoio
    
    private void validarDatasTarefa(LocalDate dataInicio, LocalDate dataFimPrevista) {
        if (dataInicio != null && dataFimPrevista != null) {
            if (dataFimPrevista.isBefore(dataInicio)) {
                throw new BusinessException("Data fim prevista não pode ser anterior à data de início");
            }
        }
    }
    
    private TarefaResponseDTO converterParaResponseDTO(Tarefa tarefa) {
        TarefaResponseDTO dto = modelMapper.map(tarefa, TarefaResponseDTO.class);
        
        // Adiciona informações do projeto
        if (tarefa.getProjeto() != null) {
            dto.setIdProjeto(tarefa.getProjeto().getId());
            dto.setNomeProjeto(tarefa.getProjeto().getNome());
        }
        
        // Calcula campos adicionais
        dto.setAtrasada(tarefa.isAtrasada());
        dto.setDiasRestantes(tarefa.getDiasRestantes());
        
        return dto;
    }
}