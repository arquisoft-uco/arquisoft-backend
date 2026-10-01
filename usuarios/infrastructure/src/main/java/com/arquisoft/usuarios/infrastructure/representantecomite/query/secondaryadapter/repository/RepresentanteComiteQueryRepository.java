package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface RepresentanteComiteQueryRepository
        extends SpecificationQueryRepository<RepresentanteComiteJpaQueryEntity, UUID> {
}
