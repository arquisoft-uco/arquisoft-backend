package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface ArtefactoSinAvanceQueryRepository
        extends SpecificationQueryRepository<ArtefactoSinAvanceJpaQueryEntity, UUID> {
}
