package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ObservacionEvaluacionAsesorQueryRepository
        extends QueryRepository<ObservacionEvaluacionAsesorJpaQueryEntity, UUID> {

    List<ObservacionEvaluacionAsesorJpaQueryEntity> findByEvaluacionFichaPerfilIdAndAsesorFichaIdOrderByObservacionAscIdAsc(
            UUID evaluacionFichaPerfilId, UUID asesorFichaId);
}
