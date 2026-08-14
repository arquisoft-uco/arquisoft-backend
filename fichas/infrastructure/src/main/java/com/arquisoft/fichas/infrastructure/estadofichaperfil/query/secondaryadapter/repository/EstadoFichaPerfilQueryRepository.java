package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.shared.postgres.repository.ReadOnlyRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilQueryRepository
        extends ReadOnlyRepository<EstadoFichaPerfilEntity, UUID> {

    Optional<EstadoFichaPerfilEntity> findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(UUID fichaPerfilId);
}
