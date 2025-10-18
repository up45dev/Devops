package com.projeto.management.service;

import com.projeto.management.dto.request.ProjetoRequestDTO;
import com.projeto.management.dto.response.ProjetoResponseDTO;
import com.projeto.management.exception.EntityNotFoundException;
import com.projeto.management.exception.BusinessException;
import com.projeto.management.model.entity.Projeto;
import com.projeto.management.model.enums.StatusProjeto;
import com.projeto.management.model.enums.Prioridade;
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
 * Service para gerenciamento de Projetos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjetoService {
    
    private final ProjetoRepository projetoRepository;
    private final ModelMapper modelMapper;
    
    /**
     * Cria um novo projeto
     */
    public ProjetoResponseDTO criarProjeto(ProjetoRequestDTO requestDTO, String usuarioLogado) {
        log.info("Criando novo projeto: {}", requestDTO.getNome());
        
        // Validações de negócio
        validarDatasProject(requestDTO.getDataInicio(), requestDTO.getDataFimPrevista());
        
        Projeto projeto = modelMapper.map(requestDTO, Projeto.class);
        projeto.setUsuarioCriacao(usuarioLogado);
        projeto.setUsuarioAtualizacao(usuarioLogado);
        
        // Define valores padrão se não informados
        if (projeto.getStatus() == null) {
            projeto.setStatus(StatusProjeto.PLANEJAMENTO);
        }
        if (projeto.getPrioridade() == null) {
            projeto.setPrioridade(Prioridade.MEDIA);
        }
        
        Projeto projetoSalvo = projetoRepository.save(projeto);
        log.info("Projeto criado com ID: {}", projetoSalvo.getId());
        
        return converterParaResponseDTO(projetoSalvo);
    }
    
    /**
     * Busca projeto por ID
     */
    @Transactional(readOnly = true)
    public ProjetoResponseDTO buscarPorId(Long id) {
        log.debug("Buscando projeto por ID: {}", id);
        
        Projeto projeto = projetoRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + id));
            
        return converterParaResponseDTO(projeto);
    }
    
    /**
     * Lista todos os projetos ativos com paginação
     */
    @Transactional(readOnly = true)
    public Page<ProjetoResponseDTO> listarProjetos(String nome, StatusProjeto status, 
                                                  String responsavel, Pageable pageable) {
        log.debug("Listando projetos com filtros - Nome: {}, Status: {}, Responsável: {}", 
                 nome, status, responsavel);
        
        Page<Projeto> projetos = projetoRepository.findProjetosComFiltros(nome, status, responsavel, pageable);
        
        return projetos.map(this::converterParaResponseDTO);
    }
    
    /**
     * Atualiza um projeto
     */
    public ProjetoResponseDTO atualizarProjeto(Long id, ProjetoRequestDTO requestDTO, String usuarioLogado) {
        log.info("Atualizando projeto ID: {}", id);
        
        Projeto projeto = projetoRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + id));
        
        // Validações de negócio
        validarDatasProject(requestDTO.getDataInicio(), requestDTO.getDataFimPrevista());
        
        // Atualiza os campos
        modelMapper.map(requestDTO, projeto);
        projeto.setUsuarioAtualizacao(usuarioLogado);
        
        Projeto projetoAtualizado = projetoRepository.save(projeto);
        log.info("Projeto atualizado: {}", projetoAtualizado.getId());
        
        return converterParaResponseDTO(projetoAtualizado);
    }
    
    /**
     * Exclui logicamente um projeto
     */
    public void excluirProjeto(Long id, String usuarioLogado) {
        log.info("Excluindo projeto ID: {}", id);
        
        Projeto projeto = projetoRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + id));
        
        // Verifica se pode excluir
        if (projeto.getStatus() == StatusProjeto.EM_ANDAMENTO) {
            throw new BusinessException("Não é possível excluir um projeto em andamento");
        }
        
        projeto.setAtivo(false);
        projeto.setUsuarioAtualizacao(usuarioLogado);
        projetoRepository.save(projeto);
        
        log.info("Projeto excluído logicamente: {}", id);
    }
    
    /**
     * Altera status do projeto
     */
    public ProjetoResponseDTO alterarStatus(Long id, StatusProjeto novoStatus, String usuarioLogado) {
        log.info("Alterando status do projeto {} para {}", id, novoStatus);
        
        Projeto projeto = projetoRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + id));
        
        StatusProjeto statusAnterior = projeto.getStatus();
        
        // Validações de transição de status
        validarTransicaoStatus(statusAnterior, novoStatus);
        
        projeto.setStatus(novoStatus);
        projeto.setUsuarioAtualizacao(usuarioLogado);
        
        // Se concluindo, define data fim real
        if (novoStatus == StatusProjeto.CONCLUIDO && projeto.getDataFimReal() == null) {
            projeto.setDataFimReal(LocalDate.now());
        }
        
        Projeto projetoAtualizado = projetoRepository.save(projeto);
        log.info("Status alterado de {} para {}", statusAnterior, novoStatus);
        
        return converterParaResponseDTO(projetoAtualizado);
    }
    
    /**
     * Busca projetos atrasados
     */
    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> buscarProjetosAtrasados() {
        log.debug("Buscando projetos atrasados");
        
        List<Projeto> projetos = projetoRepository.findProjetosAtrasados(LocalDate.now());
        
        return projetos.stream()
            .map(this::converterParaResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca projetos por responsável
     */
    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> buscarPorResponsavel(String responsavel) {
        log.debug("Buscando projetos por responsável: {}", responsavel);
        
        List<Projeto> projetos = projetoRepository.findByResponsavelContainingIgnoreCaseAndAtivoTrue(responsavel);
        
        return projetos.stream()
            .map(this::converterParaResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtém estatísticas dos projetos
     */
    @Transactional(readOnly = true)
    public Object getEstatisticasProjetos() {
        log.debug("Gerando estatísticas dos projetos");
        
        return projetoRepository.countProjetosPorStatus();
    }
    
    // Métodos privados de apoio
    
    private void validarDatasProject(LocalDate dataInicio, LocalDate dataFimPrevista) {
        if (dataInicio != null && dataFimPrevista != null) {
            if (dataFimPrevista.isBefore(dataInicio)) {
                throw new BusinessException("Data fim prevista não pode ser anterior à data de início");
            }
        }
    }
    
    private void validarTransicaoStatus(StatusProjeto statusAtual, StatusProjeto novoStatus) {
        // Regras de negócio para transição de status
        if (statusAtual == StatusProjeto.CANCELADO) {
            throw new BusinessException("Não é possível alterar status de um projeto cancelado");
        }
        
        if (statusAtual == StatusProjeto.CONCLUIDO && novoStatus != StatusProjeto.CONCLUIDO) {
            throw new BusinessException("Não é possível alterar status de um projeto concluído");
        }
    }
    
    private ProjetoResponseDTO converterParaResponseDTO(Projeto projeto) {
        ProjetoResponseDTO dto = modelMapper.map(projeto, ProjetoResponseDTO.class);
        
        // Calcula campos adicionais
        dto.setPercentualConclusao(projeto.getPercentualConclusao());
        dto.setAtrasado(projeto.isAtrasado());
        
        if (projeto.getTarefas() != null) {
            dto.setTotalTarefas(projeto.getTarefas().size());
            
            long tarefasConcluidas = projeto.getTarefas().stream()
                .filter(t -> t.getAtivo() && t.getStatus() == com.projeto.management.model.enums.StatusTarefa.CONCLUIDA)
                .count();
            dto.setTarefasConcluidas((int) tarefasConcluidas);
            
            dto.setTotalHorasEstimadas(
                projeto.getTarefas().stream()
                    .filter(t -> t.getAtivo() && t.getEstimativaHoras() != null)
                    .map(t -> t.getEstimativaHoras())
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            );
            
            dto.setTotalHorasTrabalhadas(
                projeto.getTarefas().stream()
                    .filter(t -> t.getAtivo() && t.getHorasTrabalhadas() != null)
                    .map(t -> t.getHorasTrabalhadas())
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            );
        }
        
        return dto;
    }
}
