package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstadoFichaPerfilEstudianteQueryRepository
        extends QueryRepository<EstadoFichaPerfilJpaQueryEntity, UUID> {

    List<EstadoFichaPerfilJpaQueryEntity> findByFichaPerfilIdAndEstudianteIdOrderByFechaActualizacionDesc(
            UUID fichaPerfilId, UUID estudianteId);
}
