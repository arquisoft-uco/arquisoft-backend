package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluacionFichaPerfilCoordinadorQueryRepository
        extends QueryRepository<EvaluacionFichaPerfilCoordinadorJpaQueryEntity, UUID> {

    List<EvaluacionFichaPerfilCoordinadorJpaQueryEntity> findByFichaPerfilIdOrderByFechaCreacionAsc(UUID fichaPerfilId);
}
