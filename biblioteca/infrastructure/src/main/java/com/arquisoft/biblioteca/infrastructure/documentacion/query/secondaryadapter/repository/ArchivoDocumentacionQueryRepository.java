package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface ArchivoDocumentacionQueryRepository
        extends QueryRepository<ArchivoDocumentacionJpaQueryEntity, UUID> {
}
