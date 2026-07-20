package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilJpaRepository
        extends JpaRepository<EstadoFichaPerfilJpaEntity, UUID> {

    Optional<EstadoFichaPerfilJpaEntity> findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(UUID fichaPerfilId);
}
