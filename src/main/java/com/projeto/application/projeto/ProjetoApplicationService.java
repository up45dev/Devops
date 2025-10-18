package com.projeto.application.projeto;

import com.projeto.application.projeto.commands.CriarProjetoCommand;
import com.projeto.application.projeto.commands.AtualizarProjetoCommand;
import com.projeto.application.projeto.commands.AlterarStatusProjetoCommand;
import com.projeto.domain.projeto.Projeto;
import com.projeto.domain.projeto.ProjetoRepository;
import com.projeto.domain.projeto.valueobjects.StatusProjeto;
import com.projeto.infrastructure.events.EventPublisher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Application Service para Projeto
 * Implementa casos de uso de negócio
 * Segue princípios SRP, DIP e coordenação entre domínio e infraestrutura
 */
@Service
@Transactional
public class ProjetoApplicationService {
    
    private final ProjetoRepository projetoRepository;
    private final EventPublisher eventPublisher;
    
    public ProjetoApplicationService(ProjetoRepository projetoRepository, 
                                   EventPublisher eventPublisher) {
        this.projetoRepository = projetoRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Caso de uso: Criar novo projeto
     * Aplica validações de negócio e publica eventos
     */
    public Long criarProjeto(@Valid CriarProjetoCommand command) {
        // Verificação de regra de negócio
        if (projetoRepository.existsByNome(command.nome())) {
            throw new IllegalArgumentException(
                "Já existe um projeto com o nome: " + command.nome()
            );
        }
        
        // Criação da entidade de domínio
        var projeto = Projeto.criar(
            command.nome(),
            command.descricao(),
            command.dataInicio(),
            command.dataFim(),
            command.responsavel(),
            command.criador()
        );
        
        // Persistência
        var projetoSalvo = projetoRepository.save(projeto);
        
        // Publicação de eventos de domínio
        projeto.getEventos().forEach(eventPublisher::publish);
        projeto.limparEventos();
        
        return projetoSalvo.getId();
    }
    
    /**
     * Caso de uso: Atualizar projeto existente
     */
    public void atualizarProjeto(@Valid AtualizarProjetoCommand command) {
        var projeto = buscarProjetoPorId(command.id());
        
        projeto.atualizar(
            command.nome(),
            command.descricao(),
            command.dataFim(),
            command.responsavel(),
            command.usuario()
        );
        
        projetoRepository.save(projeto);
        projeto.getEventos().forEach(eventPublisher::publish);
        projeto.limparEventos();
    }
    
    /**
     * Caso de uso: Alterar status do projeto
     */
    public void alterarStatus(@Valid AlterarStatusProjetoCommand command) {
        var projeto = buscarProjetoPorId(command.id());
        var novoStatus = StatusProjeto.of(command.status());
        
        switch (novoStatus.valor()) {
            case "EM_ANDAMENTO" -> projeto.iniciar(command.usuario());
            case "CONCLUIDO" -> projeto.finalizar(command.usuario());
            case "CANCELADO" -> projeto.cancelar(command.motivo(), command.usuario());
            default -> throw new IllegalArgumentException(
                "Transição de status não suportada: " + command.status()
            );
        }
        
        projetoRepository.save(projeto);
        projeto.getEventos().forEach(eventPublisher::publish);
        projeto.limparEventos();
    }
    
    /**
     * Caso de uso: Excluir projeto
     * Aplica regras de negócio para exclusão
     */
    public void excluirProjeto(@NotNull Long id, @NotNull String usuario) {
        var projeto = buscarProjetoPorId(id);
        
        // Regra de negócio: não pode excluir projeto com tarefas ativas
        // Esta verificação seria implementada consultando o repositório de tarefas
        
        if (projeto.getStatus().estaAtivo()) {
            throw new IllegalStateException(
                "Não é possível excluir projeto ativo. Cancele o projeto primeiro."
            );
        }
        
        projetoRepository.delete(projeto);
    }
    
    /**
     * Método auxiliar para buscar projeto com tratamento de erro
     * Aplica o princípio DRY (Don't Repeat Yourself)
     */
    private Projeto buscarProjetoPorId(Long id) {
        return projetoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Projeto não encontrado com ID: " + id
            ));
    }
}
