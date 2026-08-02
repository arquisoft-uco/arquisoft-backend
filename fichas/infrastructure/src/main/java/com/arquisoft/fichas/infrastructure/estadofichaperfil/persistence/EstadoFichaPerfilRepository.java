package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilRepository
        extends JpaRepository<EstadoFichaPerfilEntity, UUID> {

    Optional<EstadoFichaPerfilEntity> findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(UUID fichaPerfilId);
}
