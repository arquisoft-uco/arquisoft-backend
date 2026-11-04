package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface EvaluacionAsignadaJuradoQueryRepository
        extends SpecificationQueryRepository<EvaluacionAsignadaJuradoJpaQueryEntity, UUID> {
}
