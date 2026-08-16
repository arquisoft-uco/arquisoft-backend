package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;
import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoFichaPerfilQueryRepository
        extends QueryRepository<EstadoFichaPerfilJpaEntity, UUID> {

    Optional<EstadoFichaPerfilJpaEntity> findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(UUID fichaPerfilId);
}
