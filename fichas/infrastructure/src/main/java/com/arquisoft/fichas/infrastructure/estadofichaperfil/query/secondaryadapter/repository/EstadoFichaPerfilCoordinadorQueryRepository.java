package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstadoFichaPerfilCoordinadorQueryRepository
        extends QueryRepository<EstadoFichaPerfilCoordinadorJpaQueryEntity, UUID> {

    List<EstadoFichaPerfilCoordinadorJpaQueryEntity> findByFichaPerfilIdOrderByFechaActualizacionAscIdAsc(UUID fichaPerfilId);
}
