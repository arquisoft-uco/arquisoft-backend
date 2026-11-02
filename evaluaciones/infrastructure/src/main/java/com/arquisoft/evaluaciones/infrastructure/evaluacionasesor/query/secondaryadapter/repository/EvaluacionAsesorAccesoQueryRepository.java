package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionAsesorAccesoQueryRepository
        extends QueryRepository<EvaluacionAsesorAccesoJpaQueryEntity, UUID> {

    Optional<EvaluacionAsesorAccesoJpaQueryEntity> findByEvaluacionId(UUID evaluacionId);
}
