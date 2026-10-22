package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface EvaluacionEstudianteQueryRepository
        extends SpecificationQueryRepository<EvaluacionEstudianteJpaQueryEntity, UUID> {
}
