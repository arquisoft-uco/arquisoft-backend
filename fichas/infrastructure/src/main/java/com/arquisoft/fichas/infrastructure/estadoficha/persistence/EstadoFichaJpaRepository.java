package com.arquisoft.fichas.infrastructure.estadoficha.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoFichaJpaRepository extends JpaRepository<EstadoFichaJpaEntity, String> {

    Optional<EstadoFichaJpaEntity> findByNombre(String nombre);
}
