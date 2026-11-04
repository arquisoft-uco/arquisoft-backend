package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface EvaluacionAsesorRealizadaQueryRepository
        extends QueryRepository<EvaluacionAsesorRealizadaJpaQueryEntity, UUID> {}
