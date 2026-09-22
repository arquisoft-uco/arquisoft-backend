package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface EvaluacionJuradoAccesoQueryRepository
        extends QueryRepository<EvaluacionJuradoJpaQueryEntity, UUID> {
}
