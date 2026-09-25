package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface CoordinadorQueryRepository
        extends SpecificationQueryRepository<CoordinadorJpaQueryEntity, UUID> {
}
