package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface PlanRealQueryRepository
        extends SpecificationQueryRepository<PlanRealJpaQueryEntity, UUID> {
}
