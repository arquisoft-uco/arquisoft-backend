package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ObservacionEvaluacionCoordinadorQueryRepository
        extends QueryRepository<ObservacionEvaluacionCoordinadorJpaQueryEntity, UUID> {

    List<ObservacionEvaluacionCoordinadorJpaQueryEntity>
            findByFichaPerfilIdOrderByFechaEvaluacionAscEvaluacionFichaPerfilIdAscObservacionAscIdAsc(
                    UUID fichaPerfilId);
}
