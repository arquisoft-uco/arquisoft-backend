package com.arquisoft.shared.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio base para entidades JPA.
 * Todos los repositorios deben extender de esta clase.
 */
@Repository
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    // Base repository interface for all entities
}
