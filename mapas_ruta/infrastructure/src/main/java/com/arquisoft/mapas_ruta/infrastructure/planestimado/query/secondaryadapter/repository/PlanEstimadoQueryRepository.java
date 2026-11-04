package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface PlanEstimadoQueryRepository
        extends SpecificationQueryRepository<PlanEstimadoJpaQueryEntity, UUID> {
}
