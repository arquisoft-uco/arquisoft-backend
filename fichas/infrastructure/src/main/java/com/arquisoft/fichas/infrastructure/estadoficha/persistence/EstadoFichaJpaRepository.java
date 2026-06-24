package com.arquisoft.fichas.infrastructure.estadoficha.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaJpaRepository extends JpaRepository<EstadoFichaJpaEntity, UUID> {

    Optional<EstadoFichaJpaEntity> findByNombre(String nombre);
}
