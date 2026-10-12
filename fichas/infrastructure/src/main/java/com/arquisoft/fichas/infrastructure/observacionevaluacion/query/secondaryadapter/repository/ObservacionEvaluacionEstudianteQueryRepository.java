package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ObservacionEvaluacionEstudianteQueryRepository
        extends QueryRepository<ObservacionEvaluacionEstudianteJpaQueryEntity, UUID> {

    List<ObservacionEvaluacionEstudianteJpaQueryEntity> findByEvaluacionFichaPerfilIdAndEstudianteIdOrderByObservacionAscIdAsc(
            UUID evaluacionFichaPerfilId, UUID estudianteId);
}
