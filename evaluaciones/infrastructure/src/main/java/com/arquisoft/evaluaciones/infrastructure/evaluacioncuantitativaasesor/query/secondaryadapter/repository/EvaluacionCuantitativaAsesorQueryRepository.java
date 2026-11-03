package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluacionCuantitativaAsesorQueryRepository
        extends QueryRepository<EvaluacionCuantitativaAsesorJpaQueryEntity, UUID> {

    List<EvaluacionCuantitativaAsesorJpaQueryEntity> findByEvaluacionAsesorIdOrderByItemNombreAscIdAsc(
            UUID evaluacionAsesorId);
}
