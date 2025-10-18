package com.projeto.domain.tarefa.events;

import java.time.LocalDateTime;

/**
 * Eventos de domínio para Tarefa
 * Seguem o padrão de Domain Events do DDD
 * Utilizando sealed classes do Java 21
 */
public sealed interface TarefaEvent {
    
    Long tarefaId();
    LocalDateTime ocorridoEm();
    
    record TarefaCriada(
        Long tarefaId,
        String titulo,
        Long projetoId,
        LocalDateTime ocorridoEm
    ) implements TarefaEvent {
        public TarefaCriada(Long tarefaId, String titulo, Long projetoId) {
            this(tarefaId, titulo, projetoId, LocalDateTime.now());
        }
    }
    
    record TarefaIniciada(
        Long tarefaId,
        String titulo,
        Long projetoId,
        LocalDateTime ocorridoEm
    ) implements TarefaEvent {
        public TarefaIniciada(Long tarefaId, String titulo, Long projetoId) {
            this(tarefaId, titulo, projetoId, LocalDateTime.now());
        }
    }
    
    record TarefaFinalizada(
        Long tarefaId,
        String titulo,
        Long projetoId,
        LocalDateTime ocorridoEm
    ) implements TarefaEvent {
        public TarefaFinalizada(Long tarefaId, String titulo, Long projetoId) {
            this(tarefaId, titulo, projetoId, LocalDateTime.now());
        }
    }
    
    record TarefaBloqueada(
        Long tarefaId,
        String titulo,
        String motivo,
        LocalDateTime ocorridoEm
    ) implements TarefaEvent {
        public TarefaBloqueada(Long tarefaId, String titulo, String motivo) {
            this(tarefaId, titulo, motivo, LocalDateTime.now());
        }
    }
    
    record TarefaDesbloqueada(
        Long tarefaId,
        String titulo,
        LocalDateTime ocorridoEm
    ) implements TarefaEvent {
        public TarefaDesbloqueada(Long tarefaId, String titulo) {
            this(tarefaId, titulo, LocalDateTime.now());
        }
    }
    
    record TarefaAtualizada(
        Long tarefaId,
        String titulo,
        LocalDateTime ocorridoEm
    ) implements TarefaEvent {
        public TarefaAtualizada(Long tarefaId, String titulo) {
            this(tarefaId, titulo, LocalDateTime.now());
        }
    }
    
    record PercentualAtualizado(
        Long tarefaId,
        String titulo,
        Integer percentualAnterior,
        Integer novoPercentual,
        LocalDateTime ocorridoEm
    ) implements TarefaEvent {
        public PercentualAtualizado(Long tarefaId, String titulo, Integer percentualAnterior, Integer novoPercentual) {
            this(tarefaId, titulo, percentualAnterior, novoPercentual, LocalDateTime.now());
        }
    }
}
