package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface EvaluacionAccesoQueryRepository
        extends QueryRepository<EvaluacionJpaQueryEntity, UUID> {
}
