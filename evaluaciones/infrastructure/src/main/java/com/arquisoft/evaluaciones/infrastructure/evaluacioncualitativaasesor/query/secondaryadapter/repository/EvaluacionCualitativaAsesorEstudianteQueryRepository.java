package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluacionCualitativaAsesorEstudianteQueryRepository
        extends QueryRepository<EvaluacionCualitativaAsesorEstudianteJpaQueryEntity, UUID> {

    List<EvaluacionCualitativaAsesorEstudianteJpaQueryEntity> findByEvaluacionAsesorIdOrderByItemNombreAscIdAsc(
            UUID evaluacionAsesorId);
}
