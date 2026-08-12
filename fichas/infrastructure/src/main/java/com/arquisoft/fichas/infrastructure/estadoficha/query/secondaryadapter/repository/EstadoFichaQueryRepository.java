package com.arquisoft.fichas.infrastructure.estadoficha.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoFichaRepository extends JpaRepository<EstadoFichaEntity, String> {

    Optional<EstadoFichaEntity> findByNombre(String nombre);
}
