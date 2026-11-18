package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface DocumentacionQueryRepository
        extends SpecificationQueryRepository<DocumentacionJpaQueryEntity, UUID> {
}
