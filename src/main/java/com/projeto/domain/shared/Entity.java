package com.projeto.domain.shared;

import java.util.Objects;

/**
 * Classe base para entidades do domínio
 * Implementa padrão Entity do DDD
 * Segue o princípio SRP (Single Responsibility)
 */
public abstract class Entity<ID> {
    
    protected final ID id;
    
    protected Entity(ID id) {
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo");
    }
    
    public ID getId() {
        return id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Entity<?> entity = (Entity<?>) obj;
        return Objects.equals(id, entity.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * Método template para validação de regras de negócio
     * Segue o princípio OCP (Open/Closed)
     */
    protected abstract void validate();
}
