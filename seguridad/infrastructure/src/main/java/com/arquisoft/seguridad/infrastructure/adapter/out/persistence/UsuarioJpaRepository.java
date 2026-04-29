package com.arquisoft.seguridad.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Repositorio JPA para {@link UsuarioJpaEntity}.
 *
 * <p>Extiende {@link JpaSpecificationExecutor} para soportar filtros dinámicos
 * mediante la API {@code Specification} de Spring Data JPA. Esto permite construir
 * predicados opcionales en tiempo de ejecución sin necesidad de métodos derivados
 * por cada combinación de filtros.
 *
 * <p>Los métodos heredados de {@link JpaRepository} y {@link JpaSpecificationExecutor}
 * son suficientes para esta HU — no se requieren métodos adicionales.
 */
public interface UsuarioJpaRepository
        extends JpaRepository<UsuarioJpaEntity, UUID>,
                JpaSpecificationExecutor<UsuarioJpaEntity> {
}
