package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface AsesorQueryRepository
        extends SpecificationQueryRepository<AsesorJpaQueryEntity, UUID> {
}
