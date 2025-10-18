package com.projeto.domain.projeto.events;

import java.time.LocalDateTime;

/**
 * Eventos de domínio para Projeto
 * Seguem o padrão de Domain Events do DDD
 * Utilizando sealed classes do Java 21
 */
public sealed interface ProjetoEvent {
    
    Long projetoId();
    LocalDateTime ocorridoEm();
    
    record ProjetoCriado(
        Long projetoId,
        String nome,
        LocalDateTime ocorridoEm
    ) implements ProjetoEvent {
        public ProjetoCriado(Long projetoId, String nome) {
            this(projetoId, nome, LocalDateTime.now());
        }
    }
    
    record ProjetoIniciado(
        Long projetoId,
        String nome,
        LocalDateTime ocorridoEm
    ) implements ProjetoEvent {
        public ProjetoIniciado(Long projetoId, String nome) {
            this(projetoId, nome, LocalDateTime.now());
        }
    }
    
    record ProjetoFinalizado(
        Long projetoId,
        String nome,
        LocalDateTime ocorridoEm
    ) implements ProjetoEvent {
        public ProjetoFinalizado(Long projetoId, String nome) {
            this(projetoId, nome, LocalDateTime.now());
        }
    }
    
    record ProjetoCancelado(
        Long projetoId,
        String nome,
        String motivo,
        LocalDateTime ocorridoEm
    ) implements ProjetoEvent {
        public ProjetoCancelado(Long projetoId, String nome, String motivo) {
            this(projetoId, nome, motivo, LocalDateTime.now());
        }
    }
    
    record ProjetoAtualizado(
        Long projetoId,
        String nome,
        LocalDateTime ocorridoEm
    ) implements ProjetoEvent {
        public ProjetoAtualizado(Long projetoId, String nome) {
            this(projetoId, nome, LocalDateTime.now());
        }
    }
}
