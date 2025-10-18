package com.projeto.domain.shared;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object base para representar dados de auditoria
 * Segue o princípio de imutabilidade do DDD
 */
public record AuditInfo(
    LocalDateTime criadoEm,
    String criadoPor,
    LocalDateTime atualizadoEm,
    String atualizadoPor
) {
    
    public AuditInfo {
        Objects.requireNonNull(criadoEm, "Data de criação não pode ser nula");
        Objects.requireNonNull(criadoPor, "Criador não pode ser nulo");
    }
    
    public static AuditInfo criar(String usuario) {
        var agora = LocalDateTime.now();
        return new AuditInfo(agora, usuario, agora, usuario);
    }
    
    public AuditInfo atualizar(String usuario) {
        return new AuditInfo(
            this.criadoEm,
            this.criadoPor,
            LocalDateTime.now(),
            usuario
        );
    }
}
