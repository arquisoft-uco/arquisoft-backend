package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstadoFichaPerfilRepresentanteQueryRepository
        extends QueryRepository<EstadoFichaPerfilRepresentanteJpaQueryEntity, UUID> {

    List<EstadoFichaPerfilRepresentanteJpaQueryEntity> findByFichaPerfilIdAndRepresentanteComiteIdOrderByFechaActualizacionAsc(
            UUID fichaPerfilId, UUID representanteComiteId);
}
